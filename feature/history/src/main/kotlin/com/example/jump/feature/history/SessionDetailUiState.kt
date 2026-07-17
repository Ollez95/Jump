package com.example.jump.feature.history

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.WorkoutSession

data class SessionDetailUiState(
  val status: SessionDetailStatus = SessionDetailStatus.LOADING,
  val session: WorkoutSession? = null,
  val calorieEstimate: WorkoutCalorieEstimate = WorkoutCalorieEstimate(),
)

enum class SessionDetailStatus { LOADING, LOADED, NOT_FOUND, ERROR }
