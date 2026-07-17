package com.example.jump.core.domain

import com.example.jump.core.domain.repository.GamificationRepository
import com.example.jump.core.model.WorkoutRewardResult
import javax.inject.Inject

class AwardWorkoutRewardsUseCase @Inject constructor(
  private val repository: GamificationRepository,
) {
  suspend operator fun invoke(sessionId: Long): WorkoutRewardResult? =
    repository.awardForCompletedWorkout(sessionId)
}
