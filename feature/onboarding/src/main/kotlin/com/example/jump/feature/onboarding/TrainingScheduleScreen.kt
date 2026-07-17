package com.example.jump.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpChoiceCard
import com.example.jump.core.designsystem.component.layout.JumpDetailRow
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal

@Composable
internal fun TrainingScheduleScreen(
  frequency: Int,
  level: ExperienceLevel,
  goal: TrainingGoal,
  onFrequencySelected: (Int) -> Unit,
  onBack: () -> Unit,
  onFinish: () -> Unit,
) {
  OnboardingStepScaffold(
    step = 2,
    title = stringResource(R.string.onboarding_schedule_title),
    description = stringResource(R.string.onboarding_schedule_description),
    actionLabel = stringResource(R.string.action_build_plan),
    onContinue = onFinish,
    onBack = onBack,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        JumpEyebrow(stringResource(R.string.onboarding_weekly_rhythm))
        Text(
          stringResource(R.string.onboarding_sustainable_pace),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        (2..6).forEach { count ->
          JumpChoiceCard(
            label = if (count == 6) {
              stringResource(R.string.onboarding_frequency_six_plus)
            } else {
              pluralStringResource(R.plurals.sessions_per_week, count, count)
            },
            description = frequencyDescription(count),
            selected = frequency == count,
            onClick = { onFrequencySelected(count) },
          )
        }
      }
      JumpCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        JumpEyebrow(stringResource(R.string.onboarding_plan_preview))
        Text(
          stringResource(R.string.onboarding_ready_title),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        JumpDetailRow(stringResource(R.string.profile_experience), experienceLabel(level))
        JumpDetailRow(stringResource(R.string.profile_goal), goalLabel(goal))
        JumpDetailRow(
          stringResource(R.string.profile_frequency),
          pluralStringResource(R.plurals.sessions_per_week, frequency, frequency),
        )
      }
    }
  }
}

@Composable
private fun frequencyDescription(frequency: Int): String = stringResource(
  when (frequency) {
    2 -> R.string.onboarding_frequency_two_description
    3 -> R.string.onboarding_frequency_three_description
    4 -> R.string.onboarding_frequency_four_description
    5 -> R.string.onboarding_frequency_five_description
    else -> R.string.onboarding_frequency_six_description
  },
)

@JumpLightDarkPreviews
@Composable
private fun TrainingScheduleScreenPreview() {
  JumpTheme {
    TrainingScheduleScreen(
      frequency = 4,
      level = ExperienceLevel.REGULAR,
      goal = TrainingGoal.ENDURANCE,
      onFrequencySelected = {},
      onBack = {},
      onFinish = {},
    )
  }
}
