package com.example.jump.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpChoiceCard
import com.example.jump.core.designsystem.component.layout.JumpDetailRow
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.component.navigation.JumpTopAppBar
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile

@Composable
fun TrainingProfileScreen(
  profile: UserProfile,
  level: ExperienceLevel,
  goal: TrainingGoal,
  frequency: Int,
  onBack: () -> Unit,
  onLevelSelected: (ExperienceLevel) -> Unit,
  onGoalSelected: (TrainingGoal) -> Unit,
  onFrequencySelected: (Int) -> Unit,
  onSave: (UserProfile) -> Unit,
) {
  JumpScreen(
    topBar = {
      JumpTopAppBar(
        title = stringResource(R.string.training_profile),
        onBack = onBack,
        backContentDescription = stringResource(R.string.back_to_profile),
      )
    },
  ) {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = stringResource(R.string.adaptive_plan),
          title = stringResource(R.string.edit_profile_title),
          description = stringResource(R.string.edit_profile_description),
        )
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.experience_label))
          ExperienceLevel.entries.forEach { option ->
            JumpChoiceCard(
              label = experienceLabel(option),
              description = experienceDescription(option),
              selected = level == option,
              onClick = { onLevelSelected(option) },
            )
          }
        }
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.goal_label))
          TrainingGoal.entries.forEach { option ->
            JumpChoiceCard(
              label = goalLabel(option),
              description = goalDescription(option),
              selected = goal == option,
              onClick = { onGoalSelected(option) },
            )
          }
        }
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.frequency_label))
          Text(
            pluralStringResource(R.plurals.sessions_per_week, frequency, frequency),
            style = MaterialTheme.typography.titleLarge,
          )
          Text(
            stringResource(R.string.frequency_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          (2..6).forEach { count ->
            JumpChoiceCard(
              label = pluralStringResource(R.plurals.sessions_per_week, count, count),
              description = frequencyDescription(count),
              selected = frequency == count,
              onClick = { onFrequencySelected(count) },
            )
          }
        }
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.updated_plan))
          JumpDetailRow(stringResource(R.string.experience_label), experienceLabel(level))
          JumpDetailRow(stringResource(R.string.goal_label), goalLabel(goal))
          JumpDetailRow(
            stringResource(R.string.frequency_label),
            pluralStringResource(R.plurals.sessions_per_week, frequency, frequency),
          )
        }
      }
      item {
        JumpPrimaryButton(
          label = stringResource(R.string.save_training_profile),
          onClick = {
            onSave(
              profile.copy(
                onboardingComplete = true,
                experienceLevel = level,
                trainingGoal = goal,
                sessionsPerWeek = frequency,
              ),
            )
          },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}

@Composable
private fun frequencyDescription(frequency: Int): String = stringResource(
  when (frequency) {
    2 -> R.string.frequency_two_description
    3 -> R.string.frequency_three_description
    4 -> R.string.frequency_four_description
    5 -> R.string.frequency_five_description
    else -> R.string.frequency_six_description
  },
)

@JumpLightDarkPreviews
@Preview(name = "1.5x font", widthDp = 390, heightDp = 884, fontScale = 1.5f, showBackground = true)
@Composable
private fun TrainingProfileScreenPreview() {
  val profile = UserProfile(
    onboardingComplete = true,
    experienceLevel = ExperienceLevel.REGULAR,
    trainingGoal = TrainingGoal.ENDURANCE,
    sessionsPerWeek = 4,
  )
  JumpTheme {
    TrainingProfileScreen(
      profile = profile,
      level = profile.experienceLevel,
      goal = profile.trainingGoal,
      frequency = profile.sessionsPerWeek,
      onBack = {},
      onLevelSelected = {},
      onGoalSelected = {},
      onFrequencySelected = {},
      onSave = {},
    )
  }
}
