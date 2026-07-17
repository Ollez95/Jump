package com.example.jump.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.button.JumpSecondaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.indicator.JumpProgress
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme

@Composable
internal fun OnboardingStepScaffold(
  step: Int,
  title: String,
  description: String,
  actionLabel: String,
  onContinue: () -> Unit,
  onBack: (() -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  JumpScreen(
    bottomBar = {
      OnboardingFooter(
        actionLabel = actionLabel,
        onContinue = onContinue,
        onBack = onBack,
      )
    },
  ) {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 22.dp, vertical = 28.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          JumpHeader(
            eyebrow = stringResource(R.string.onboarding_welcome),
            title = title,
            description = description,
            brandMark = true,
          )
          JumpEyebrow(
            stringResource(R.string.onboarding_step, step + 1, ONBOARDING_STEP_COUNT),
          )
          JumpProgress((step + 1f) / ONBOARDING_STEP_COUNT)
        }
      }
      item { content() }
    }
  }
}

@Composable
private fun OnboardingFooter(
  actionLabel: String,
  onContinue: () -> Unit,
  onBack: (() -> Unit)?,
) {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
    Row(
      Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(horizontal = 22.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      if (onBack != null) {
        JumpSecondaryButton(
          label = stringResource(R.string.action_back),
          onClick = onBack,
          modifier = Modifier.weight(1f),
        )
      }
      JumpPrimaryButton(
        label = actionLabel,
        onClick = onContinue,
        modifier = Modifier.weight(if (onBack != null) 1.6f else 1f),
      )
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun OnboardingStepScaffoldPreview() {
  JumpTheme {
    OnboardingStepScaffold(
      step = 0,
      title = stringResource(R.string.onboarding_profile_title),
      description = stringResource(R.string.onboarding_profile_description),
      actionLabel = stringResource(R.string.action_continue),
      onContinue = {},
    ) {
      JumpCard {
        Text(
          stringResource(R.string.onboarding_starting_point),
          style = MaterialTheme.typography.titleLarge,
        )
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun OnboardingFooterPreview() {
  JumpTheme {
    OnboardingFooter(
      actionLabel = stringResource(R.string.action_continue),
      onContinue = {},
      onBack = {},
    )
  }
}
