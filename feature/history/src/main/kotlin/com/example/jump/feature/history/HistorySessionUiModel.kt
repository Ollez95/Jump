package com.example.jump.feature.history

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.WorkoutSession

data class HistorySessionUiModel(
  val session: WorkoutSession,
  val calorieEstimate: WorkoutCalorieEstimate,
)
