package com.example.jump.feature.history

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.WorkoutSession

data class SessionDetailUiState(
  val session: WorkoutSession? = null,
  val calorieEstimate: WorkoutCalorieEstimate = WorkoutCalorieEstimate(),
)
