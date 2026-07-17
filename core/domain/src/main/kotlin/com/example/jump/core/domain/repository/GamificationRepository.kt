package com.example.jump.core.domain.repository

import com.example.jump.core.model.GamificationState
import com.example.jump.core.model.WorkoutRewardResult
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
  val state: Flow<GamificationState>

  suspend fun awardForCompletedWorkout(sessionId: Long): WorkoutRewardResult?
  suspend fun rewardForWorkout(sessionId: Long): WorkoutRewardResult?
}
