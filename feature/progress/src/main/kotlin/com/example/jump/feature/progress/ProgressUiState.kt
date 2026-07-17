package com.example.jump.feature.progress

import com.example.jump.core.domain.WorkoutProgressReport
import com.example.jump.core.model.GamificationState

data class ProgressUiState(
  val report: WorkoutProgressReport? = null,
  val rewards: GamificationState? = null,
  val loadFailed: Boolean = false,
)
