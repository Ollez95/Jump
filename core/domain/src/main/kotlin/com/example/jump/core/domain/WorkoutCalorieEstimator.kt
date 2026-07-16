package com.example.jump.core.domain

import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.WorkoutSession
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Estimates calories for active rope-jumping time using the standard MET formula.
 *
 * The range represents slow (8.3 MET) through fast (12.3 MET) rope jumping from
 * the 2024 Adult Compendium of Physical Activities. Recovery intervals are not
 * included because their energy cost varies substantially between people.
 */
class WorkoutCalorieEstimator @Inject constructor() {
  fun estimate(
    configuration: IntervalWorkoutConfig,
    referenceWeightKg: Int = WorkoutCalorieEstimate.DEFAULT_REFERENCE_WEIGHT_KG,
  ): WorkoutCalorieEstimate {
    val config = configuration.normalized()
    return estimateActiveTime(config.activeSeconds, referenceWeightKg)
  }

  fun estimateActiveTime(
    activeSeconds: Int,
    referenceWeightKg: Int = WorkoutCalorieEstimate.DEFAULT_REFERENCE_WEIGHT_KG,
  ): WorkoutCalorieEstimate = estimateActiveSeconds(activeSeconds.toDouble(), referenceWeightKg)

  fun estimate(
    session: WorkoutSession,
    referenceWeightKg: Int = WorkoutCalorieEstimate.DEFAULT_REFERENCE_WEIGHT_KG,
  ): WorkoutCalorieEstimate = estimateActiveSeconds(session.activeMillis / 1_000.0, referenceWeightKg)

  private fun estimateActiveSeconds(
    activeSeconds: Double,
    referenceWeightKg: Int,
  ): WorkoutCalorieEstimate {
    if (activeSeconds <= 0 || referenceWeightKg <= 0) {
      return WorkoutCalorieEstimate(referenceWeightKg = referenceWeightKg.coerceAtLeast(0))
    }

    val activeMinutes = activeSeconds / 60.0
    fun calories(met: Double): Int =
      (met * 3.5 * referenceWeightKg / 200.0 * activeMinutes).roundToInt().coerceAtLeast(1)

    return WorkoutCalorieEstimate(
      minimumCalories = calories(SLOW_ROPE_JUMPING_MET),
      maximumCalories = calories(FAST_ROPE_JUMPING_MET),
      referenceWeightKg = referenceWeightKg,
    )
  }

  private companion object {
    const val SLOW_ROPE_JUMPING_MET = 8.3
    const val FAST_ROPE_JUMPING_MET = 12.3
  }
}
