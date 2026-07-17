package com.example.jump.core.workout

import com.example.jump.core.domain.repository.GamificationRepository
import com.example.jump.core.model.GamificationState
import com.example.jump.core.model.WorkoutRewardResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeGamificationRepository : GamificationRepository {
  override val state: Flow<GamificationState> = MutableStateFlow(GamificationState())
  val awardedSessionIds = mutableListOf<Long>()
  var failure: Exception? = null

  override suspend fun awardForCompletedWorkout(sessionId: Long): WorkoutRewardResult? {
    failure?.let { throw it }
    awardedSessionIds += sessionId
    return null
  }

  override suspend fun rewardForWorkout(sessionId: Long): WorkoutRewardResult? = null
}
