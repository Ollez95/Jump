package com.example.jump.ui

import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.WorkoutPlan

internal data class PendingWorkoutStart(
  val plan: WorkoutPlan,
  val countingMode: CountingMode,
)
