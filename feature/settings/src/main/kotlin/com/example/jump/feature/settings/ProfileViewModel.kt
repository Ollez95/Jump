package com.example.jump.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.account.AccountBackupService
import com.example.jump.core.account.AccountRepository
import com.example.jump.core.account.AccountSyncService
import com.example.jump.core.account.AccountSession
import com.example.jump.core.account.BackupResult
import com.example.jump.core.account.BackupStatus
import com.example.jump.core.account.AccountSyncResult
import com.example.jump.core.account.AccountSyncStatus
import com.example.jump.core.account.SignOutResult
import com.example.jump.core.account.SyncReason
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@HiltViewModel
class ProfileViewModel @Inject constructor(
  private val preferences: UserPreferencesRepository,
  private val accounts: AccountRepository,
  private val backups: AccountBackupService,
  private val sync: AccountSyncService,
) : ViewModel() {
  private val profileSavedEvents = Channel<Unit>(Channel.BUFFERED)
  val profileSaved = profileSavedEvents.receiveAsFlow()

  val profile = preferences.profile.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    UserProfile(onboardingComplete = true),
  )
  val cues = preferences.cues.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    CuePreferences(),
  )
  val countingMode = preferences.countingMode.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    CountingMode.MOTION,
  )
  val accountSession = accounts.session
    .map<AccountSession, AccountSession?> { it }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
  val backupStatus = backups.status.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    BackupStatus.Disabled,
  )
  val syncStatus = sync.status.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    AccountSyncStatus.Disabled,
  )
  private val mutableAccountAction = MutableStateFlow<ProfileAccountAction?>(null)
  val accountAction = mutableAccountAction.asStateFlow()

  fun updateCues(cues: CuePreferences) = viewModelScope.launch {
    preferences.setCuePreferences(cues)
  }

  fun updateCountingMode(mode: CountingMode) = viewModelScope.launch {
    preferences.setCountingMode(mode)
  }

  fun backupNow() = viewModelScope.launch {
    mutableAccountAction.value = ProfileAccountAction.Working
    mutableAccountAction.value = try {
      backups.backup().toProfileAccountAction()
    } catch (cancellation: CancellationException) {
      mutableAccountAction.value = null
      throw cancellation
    } catch (_: Exception) {
      ProfileAccountAction.Failed
    }
  }

  fun syncNow() = viewModelScope.launch {
    mutableAccountAction.value = ProfileAccountAction.Working
    mutableAccountAction.value = try {
      sync.sync(SyncReason.USER_REQUEST).toProfileAccountAction()
    } catch (cancellation: CancellationException) {
      mutableAccountAction.value = null
      throw cancellation
    } catch (_: Exception) {
      ProfileAccountAction.Failed
    }
  }

  fun signOut() = viewModelScope.launch {
    mutableAccountAction.value = ProfileAccountAction.Working
    mutableAccountAction.value = try {
      accounts.signOut().toProfileAccountAction()
    } catch (cancellation: CancellationException) {
      mutableAccountAction.value = null
      throw cancellation
    } catch (_: Exception) {
      ProfileAccountAction.Failed
    }
  }

  fun clearAccountAction() {
    mutableAccountAction.value = null
  }

  fun updateTrainingProfile(profile: UserProfile) =
    viewModelScope.launch {
      preferences.saveProfile(profile.copy(onboardingComplete = true))
      profileSavedEvents.send(Unit)
    }
}

enum class ProfileAccountAction {
  Working,
  BackupComplete,
  SyncComplete,
  SyncPartial,
  SignedOut,
  Unavailable,
  Failed,
}

internal fun BackupResult.toProfileAccountAction(): ProfileAccountAction = when (this) {
  is BackupResult.Completed -> ProfileAccountAction.BackupComplete
  is BackupResult.Failure -> ProfileAccountAction.Unavailable
}

internal fun AccountSyncResult.toProfileAccountAction(): ProfileAccountAction = when (this) {
  is AccountSyncResult.Completed -> ProfileAccountAction.SyncComplete
  is AccountSyncResult.Partial -> ProfileAccountAction.SyncPartial
  is AccountSyncResult.Failure -> ProfileAccountAction.Unavailable
}

internal fun SignOutResult.toProfileAccountAction(): ProfileAccountAction = when (this) {
  is SignOutResult.SignedOut -> ProfileAccountAction.SignedOut
  is SignOutResult.Failure -> ProfileAccountAction.Failed
}
