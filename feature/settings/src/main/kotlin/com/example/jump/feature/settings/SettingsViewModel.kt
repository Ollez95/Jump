package com.example.jump.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val preferences: UserPreferencesRepository,
) : ViewModel() {
  val profile = preferences.profile.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    UserProfile(),
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

  fun updateCues(cues: CuePreferences) = viewModelScope.launch {
    preferences.setCuePreferences(cues)
  }

  fun updateCountingMode(mode: CountingMode) = viewModelScope.launch {
    preferences.setCountingMode(mode)
  }

  fun resetOnboarding() = viewModelScope.launch {
    preferences.resetOnboarding()
  }
}
