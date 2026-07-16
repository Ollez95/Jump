package com.example.jump.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
  private val preferences: UserPreferencesRepository,
) : ViewModel() {
  fun finish(profile: UserProfile) = viewModelScope.launch {
    preferences.saveProfile(profile)
  }
}
