package com.example.jump.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal

@Composable
internal fun experienceLabel(level: ExperienceLevel): String = stringResource(
  when (level) {
    ExperienceLevel.BEGINNER -> R.string.experience_beginner
    ExperienceLevel.REGULAR -> R.string.experience_regular
    ExperienceLevel.ADVANCED -> R.string.experience_advanced
  },
)

@Composable
internal fun experienceDescription(level: ExperienceLevel): String = stringResource(
  when (level) {
    ExperienceLevel.BEGINNER -> R.string.experience_beginner_description
    ExperienceLevel.REGULAR -> R.string.experience_regular_description
    ExperienceLevel.ADVANCED -> R.string.experience_advanced_description
  },
)

@Composable
internal fun goalLabel(goal: TrainingGoal): String = stringResource(
  when (goal) {
    TrainingGoal.CONSISTENCY -> R.string.goal_consistency
    TrainingGoal.ENDURANCE -> R.string.goal_endurance
    TrainingGoal.SPEED -> R.string.goal_speed
  },
)

@Composable
internal fun goalDescription(goal: TrainingGoal): String = stringResource(
  when (goal) {
    TrainingGoal.CONSISTENCY -> R.string.goal_consistency_description
    TrainingGoal.ENDURANCE -> R.string.goal_endurance_description
    TrainingGoal.SPEED -> R.string.goal_speed_description
  },
)
