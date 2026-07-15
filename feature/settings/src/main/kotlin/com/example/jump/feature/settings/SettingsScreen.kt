package com.example.jump.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.jump.core.data.repository.UserPreferencesRepository
import com.example.jump.core.designsystem.component.JumpCard
import com.example.jump.core.designsystem.component.JumpDetailRow
import com.example.jump.core.designsystem.component.JumpEyebrow
import com.example.jump.core.designsystem.component.JumpHeader
import com.example.jump.core.designsystem.component.JumpInfoBanner
import com.example.jump.core.designsystem.component.JumpScreen
import com.example.jump.core.designsystem.component.JumpSecondaryButton
import com.example.jump.core.designsystem.component.JumpSettingRow
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val preferences: UserPreferencesRepository) : ViewModel() {
  val profile = preferences.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile())
  val cues = preferences.cues.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CuePreferences())
  fun updateCues(cues: CuePreferences) = viewModelScope.launch { preferences.setCuePreferences(cues) }
  fun resetOnboarding() = viewModelScope.launch { preferences.resetOnboarding() }
}

@Composable
fun SettingsRoute(viewModel: SettingsViewModel = hiltViewModel()) {
  val profile by viewModel.profile.collectAsStateWithLifecycle(); val cues by viewModel.cues.collectAsStateWithLifecycle()
  SettingsScreen(profile, cues, viewModel::updateCues, viewModel::resetOnboarding)
}

@Composable
fun SettingsScreen(profile: UserProfile, cues: CuePreferences, onCues: (CuePreferences) -> Unit, onReset: () -> Unit) {
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = "Make it yours",
          title = "Settings",
          description = "Tune your coaching cues and training plan.",
          brandMark = true,
        )
      }
      item {
        JumpCard {
          JumpEyebrow("Coaching cues")
          JumpSettingRow("Voice coaching", "Spoken prompts during intervals", cues.voiceEnabled, onCheckedChange = { onCues(cues.copy(voiceEnabled = it)) })
          JumpSettingRow("Transition tones", "Audio cues when work and rest change", cues.tonesEnabled, onCheckedChange = { onCues(cues.copy(tonesEnabled = it)) })
          JumpSettingRow("Vibration", "Haptic confirmation for key moments", cues.vibrationEnabled, onCheckedChange = { onCues(cues.copy(vibrationEnabled = it)) })
        }
      }
      item {
        JumpCard {
          JumpEyebrow("Your plan")
          Text("Training profile", style = MaterialTheme.typography.titleLarge)
          JumpDetailRow("Experience", profile.experienceLevel.pretty())
          JumpDetailRow("Goal", profile.trainingGoal.pretty())
          JumpDetailRow("Frequency", "${profile.sessionsPerWeek} sessions/week")
          JumpSecondaryButton("Change plan answers", onReset, Modifier.fillMaxWidth())
        }
      }
      item {
        JumpInfoBanner(
          title = "Pocket counting",
          message = "Secure your phone close to your body. Counts represent body jumps, so a double-under counts as one.",
        )
      }
    }
  }
}
private fun ExperienceLevel.pretty() = name.lowercase().replaceFirstChar { it.uppercase() }
private fun TrainingGoal.pretty() = name.lowercase().replaceFirstChar { it.uppercase() }
