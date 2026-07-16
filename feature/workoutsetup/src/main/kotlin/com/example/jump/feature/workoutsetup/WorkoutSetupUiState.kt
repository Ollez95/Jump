package com.example.jump.feature.workoutsetup

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalWorkoutConfig

data class WorkoutSetupUiState(
  val configuration: IntervalWorkoutConfig = IntervalWorkoutConfig(),
  val countingMode: CountingMode = CountingMode.MOTION,
  val activeWorkout: ActiveWorkoutState = ActiveWorkoutState(),
  val calorieEstimate: WorkoutCalorieEstimate = WorkoutCalorieEstimate(),
)
