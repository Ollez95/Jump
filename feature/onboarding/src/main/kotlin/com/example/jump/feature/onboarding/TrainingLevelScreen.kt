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
import com.example.jump.core.model.ExperienceLevel

@Composable
internal fun TrainingLevelScreen(
  selected: ExperienceLevel,
  onSelected: (ExperienceLevel) -> Unit,
  onContinue: () -> Unit,
) {
  OnboardingStepScaffold(
    step = 0,
    title = stringResource(R.string.onboarding_profile_title),
    description = stringResource(R.string.onboarding_profile_description),
    actionLabel = stringResource(R.string.action_continue),
    onContinue = onContinue,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      JumpEyebrow(stringResource(R.string.onboarding_starting_point))
      ExperienceLevel.entries.forEach { level ->
        JumpChoiceCard(
          label = experienceLabel(level),
          description = experienceDescription(level),
          selected = selected == level,
          onClick = { onSelected(level) },
        )
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun TrainingLevelScreenPreview() {
  JumpTheme {
    TrainingLevelScreen(
      selected = ExperienceLevel.BEGINNER,
      onSelected = {},
      onContinue = {},
    )
  }
}
