package com.example.jump.core.domain

import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutSession
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutCalorieEstimatorTest {
  private val estimator = WorkoutCalorieEstimator()

  @Test fun estimateUsesActiveJumpTimeAndStandardWeight() {
    val estimate = estimator.estimate(
      IntervalWorkoutConfig(jumpSeconds = 60, restSeconds = 30, rounds = 10),
    )

    assertEquals(102, estimate.minimumCalories)
    assertEquals(151, estimate.maximumCalories)
    assertEquals(70, estimate.referenceWeightKg)
  }

  @Test fun restTimeDoesNotInflateActiveCalorieEstimate() {
    val withoutRest = estimator.estimate(IntervalWorkoutConfig(jumpSeconds = 30, restSeconds = 0, rounds = 10))
    val withRest = estimator.estimate(IntervalWorkoutConfig(jumpSeconds = 30, restSeconds = 180, rounds = 10))

    assertEquals(withoutRest, withRest)
  }

  @Test fun estimateScalesWithReferenceWeight() {
    val estimate = estimator.estimateActiveTime(activeSeconds = 600, referenceWeightKg = 100)

    assertEquals(145, estimate.minimumCalories)
    assertEquals(215, estimate.maximumCalories)
  }

  @Test fun completedSessionUsesActualActiveTime() {
    val session = WorkoutSession(
      planId = "custom",
      title = "Custom intervals",
      kind = WorkoutKind.CUSTOM,
      startedAtEpochMillis = 1L,
      durationMillis = 900_000L,
      activeMillis = 600_000L,
      metrics = JumpMetrics(),
      status = SessionStatus.COMPLETED,
    )

    val estimate = estimator.estimate(session)

    assertEquals(102, estimate.minimumCalories)
    assertEquals(151, estimate.maximumCalories)
  }
}
