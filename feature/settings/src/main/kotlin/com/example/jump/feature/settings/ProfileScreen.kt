package com.example.jump.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpChoiceCard
import com.example.jump.core.designsystem.component.card.JumpHeroCard
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.input.JumpSettingRow
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile

@Composable
fun ProfileScreen(
  profile: UserProfile,
  cues: CuePreferences,
  countingMode: CountingMode,
  onEditTrainingProfile: () -> Unit,
  onCues: (CuePreferences) -> Unit,
  onCountingMode: (CountingMode) -> Unit,
) {
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = stringResource(R.string.profile_eyebrow),
          title = stringResource(R.string.profile_title),
          description = stringResource(R.string.profile_description),
        )
      }
      item {
        JumpHeroCard(
          eyebrow = stringResource(R.string.training_profile),
          title = experienceLabel(profile.experienceLevel),
          description = stringResource(
            R.string.training_profile_summary,
            goalLabel(profile.trainingGoal),
            pluralStringResource(
              R.plurals.sessions_per_week,
              profile.sessionsPerWeek,
              profile.sessionsPerWeek,
            ),
          ),
          actionLabel = stringResource(R.string.edit_training_profile),
          onAction = onEditTrainingProfile,
          meta = stringResource(R.string.profile_meta),
        )
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.default_counting_method))
          JumpChoiceCard(
            label = stringResource(R.string.counting_pocket),
            description = stringResource(R.string.counting_pocket_description),
            selected = countingMode == CountingMode.MOTION,
            onClick = { onCountingMode(CountingMode.MOTION) },
          )
          JumpChoiceCard(
            label = stringResource(R.string.counting_camera),
            description = stringResource(R.string.counting_camera_description),
            selected = countingMode == CountingMode.CAMERA,
            onClick = { onCountingMode(CountingMode.CAMERA) },
          )
        }
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.coaching_cues))
          JumpSettingRow(
            stringResource(R.string.voice_coaching),
            stringResource(R.string.voice_coaching_description),
            cues.voiceEnabled,
            onCheckedChange = { onCues(cues.copy(voiceEnabled = it)) },
          )
          JumpSettingRow(
            stringResource(R.string.transition_tones),
            stringResource(R.string.transition_tones_description),
            cues.tonesEnabled,
            onCheckedChange = { onCues(cues.copy(tonesEnabled = it)) },
          )
          JumpSettingRow(
            stringResource(R.string.vibration),
            stringResource(R.string.vibration_description),
            cues.vibrationEnabled,
            onCheckedChange = { onCues(cues.copy(vibrationEnabled = it)) },
          )
        }
      }
      item {
        JumpInfoBanner(
          title = stringResource(R.string.pocket_counting_title),
          message = stringResource(R.string.pocket_counting_message),
        )
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun ProfileScreenPreview() {
  JumpTheme {
    ProfileScreen(
      profile = UserProfile(
        onboardingComplete = true,
        experienceLevel = ExperienceLevel.REGULAR,
        trainingGoal = TrainingGoal.ENDURANCE,
        sessionsPerWeek = 4,
      ),
      cues = CuePreferences(),
      countingMode = CountingMode.MOTION,
      onEditTrainingProfile = {},
      onCues = {},
      onCountingMode = {},
    )
  }
}
