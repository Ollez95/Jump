package com.example.jump.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile

internal const val ONBOARDING_STEP_COUNT = 3

@Composable
fun OnboardingRoute(viewModel: OnboardingViewModel = hiltViewModel()) {
  var step by rememberSaveable { mutableIntStateOf(0) }
  var level by rememberSaveable { mutableStateOf(ExperienceLevel.BEGINNER) }
  var goal by rememberSaveable { mutableStateOf(TrainingGoal.CONSISTENCY) }
  var frequency by rememberSaveable { mutableIntStateOf(3) }

  OnboardingScreen(
    step = step,
    level = level,
    goal = goal,
    frequency = frequency,
    onLevelSelected = { level = it },
    onGoalSelected = { goal = it },
    onFrequencySelected = { frequency = it },
    onBack = { step = (step - 1).coerceAtLeast(0) },
    onContinue = {
      if (step < ONBOARDING_STEP_COUNT - 1) {
        step += 1
      } else {
        viewModel.finish(UserProfile(true, level, goal, frequency))
      }
    },
  )
}
