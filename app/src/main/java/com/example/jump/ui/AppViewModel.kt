package com.example.jump.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.account.AccountErrorCode
import com.example.jump.core.account.AccountProviderId
import com.example.jump.core.account.AccountRepository
import com.example.jump.core.account.SignInRequest
import com.example.jump.core.account.SignInResult
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.permissions.WorkoutPermissionManager
import com.example.jump.core.workout.WorkoutController
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WelcomeUiState(
  val hasEnteredApp: Boolean = false,
  val actionInProgress: Boolean = false,
  val accountError: AccountErrorCode? = null,
)

@HiltViewModel
class AppViewModel @Inject constructor(
  private val preferences: UserPreferencesRepository,
  private val accounts: AccountRepository,
  coordinator: WorkoutCoordinator,
  private val controller: WorkoutController,
  private val permissions: WorkoutPermissionManager,
) : ViewModel() {
  private val welcomeAction = MutableStateFlow(WelcomeUiState())
  private var pendingWorkoutStart: PendingWorkoutStart? = null
  val welcomeState: StateFlow<WelcomeUiState> = combine(
    preferences.welcomeComplete,
    welcomeAction,
  ) { complete, action -> action.copy(hasEnteredApp = complete || action.hasEnteredApp) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WelcomeUiState())
  val profile: StateFlow<UserProfile?> = preferences.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
  val active: StateFlow<ActiveWorkoutState> = coordinator.state

  fun prepareWorkoutStart(plan: WorkoutPlan, countingMode: CountingMode): List<String> {
    pendingWorkoutStart = PendingWorkoutStart(plan, countingMode)
    return permissions.missingPermissions(countingMode)
  }

  fun startPendingWorkoutIfPermitted(): Boolean {
    val pending = pendingWorkoutStart ?: return false
    if (!permissions.hasCountingPermission(pending.countingMode)) return false
    controller.start(pending.plan, pending.countingMode)
    pendingWorkoutStart = null
    return true
  }

  fun discardPendingWorkoutStart(): CountingMode? =
    pendingWorkoutStart?.countingMode.also { pendingWorkoutStart = null }

  fun continueAsGuest() {
    if (welcomeAction.value.actionInProgress) return
    welcomeAction.value = welcomeAction.value.copy(actionInProgress = true, accountError = null)
    viewModelScope.launch { completeWelcomeEntry() }
  }

  fun signInWithGoogle() = signIn(GOOGLE_PROVIDER)

  fun signInWithEmail() = signIn(EMAIL_PROVIDER)

  private fun signIn(provider: AccountProviderId) {
    if (welcomeAction.value.actionInProgress) return
    viewModelScope.launch {
      welcomeAction.value = welcomeAction.value.copy(actionInProgress = true, accountError = null)
      when (val result = accounts.signIn(SignInRequest(provider))) {
        is SignInResult.SignedIn -> completeWelcomeEntry()
        is SignInResult.Failure -> {
          welcomeAction.value = welcomeAction.value.copy(
            actionInProgress = false,
            accountError = result.error.code,
          )
        }
      }
    }
  }

  private suspend fun completeWelcomeEntry() {
    runCatching { preferences.setWelcomeComplete() }
      .onSuccess { welcomeAction.value = WelcomeUiState(hasEnteredApp = true) }
      .onFailure {
        welcomeAction.value = WelcomeUiState(accountError = AccountErrorCode.STORAGE)
      }
  }

  private companion object {
    val GOOGLE_PROVIDER = AccountProviderId("google")
    val EMAIL_PROVIDER = AccountProviderId("email")
  }
}
