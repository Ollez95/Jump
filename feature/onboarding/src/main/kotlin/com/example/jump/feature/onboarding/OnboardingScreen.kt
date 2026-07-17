package com.example.jump.feature.onboarding

import androidx.compose.runtime.Composable
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal

@Composable
fun OnboardingScreen(
  step: Int,
  level: ExperienceLevel,
  goal: TrainingGoal,
  frequency: Int,
  onLevelSelected: (ExperienceLevel) -> Unit,
  onGoalSelected: (TrainingGoal) -> Unit,
  onFrequencySelected: (Int) -> Unit,
  onBack: () -> Unit,
  onContinue: () -> Unit,
) {
  when (step) {
    0 -> TrainingLevelScreen(
      selected = level,
      onSelected = onLevelSelected,
      onContinue = onContinue,
    )

    1 -> TrainingGoalScreen(
      selected = goal,
      onSelected = onGoalSelected,
      onBack = onBack,
      onContinue = onContinue,
    )

    else -> TrainingScheduleScreen(
      frequency = frequency,
      level = level,
      goal = goal,
      onFrequencySelected = onFrequencySelected,
      onBack = onBack,
      onFinish = onContinue,
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun OnboardingScreenPreview() {
  JumpTheme {
    OnboardingScreen(
      step = 0,
      level = ExperienceLevel.BEGINNER,
      goal = TrainingGoal.CONSISTENCY,
      frequency = 3,
      onLevelSelected = {},
      onGoalSelected = {},
      onFrequencySelected = {},
      onBack = {},
      onContinue = {},
    )
  }
}
