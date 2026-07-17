package com.example.jump.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.component.card.JumpChoiceCard
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.TrainingGoal

@Composable
internal fun TrainingGoalScreen(
  selected: TrainingGoal,
  onSelected: (TrainingGoal) -> Unit,
  onBack: () -> Unit,
  onContinue: () -> Unit,
) {
  OnboardingStepScaffold(
    step = 1,
    title = stringResource(R.string.onboarding_goal_title),
    description = stringResource(R.string.onboarding_goal_description),
    actionLabel = stringResource(R.string.action_continue),
    onContinue = onContinue,
    onBack = onBack,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      JumpEyebrow(stringResource(R.string.onboarding_focus))
      TrainingGoal.entries.forEach { goal ->
        JumpChoiceCard(
          label = goalLabel(goal),
          description = goalDescription(goal),
          selected = selected == goal,
          onClick = { onSelected(goal) },
        )
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun TrainingGoalScreenPreview() {
  JumpTheme {
    TrainingGoalScreen(
      selected = TrainingGoal.ENDURANCE,
      onSelected = {},
      onBack = {},
      onContinue = {},
    )
  }
}
