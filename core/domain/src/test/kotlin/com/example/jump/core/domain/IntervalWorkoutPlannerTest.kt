package com.example.jump.core.domain

import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.WorkoutKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class IntervalWorkoutPlannerTest {
  private val planner = IntervalWorkoutPlanner()

  @Test fun configuredRoundsAlternateJumpAndRestWithoutTrailingRest() {
    val plan = planner.createPlan(IntervalWorkoutConfig(jumpSeconds = 40, restSeconds = 15, rounds = 3))

    assertEquals(WorkoutKind.CUSTOM, plan.kind)
    assertEquals(
      listOf(IntervalType.WORK, IntervalType.REST, IntervalType.WORK, IntervalType.REST, IntervalType.WORK),
      plan.intervals.map { it.type },
    )
    assertEquals(listOf(40, 15, 40, 15, 40), plan.intervals.map { it.durationSeconds })
    assertEquals(150, plan.durationSeconds)
  }

  @Test fun zeroRestCreatesBackToBackJumpRounds() {
    val plan = planner.createPlan(IntervalWorkoutConfig(jumpSeconds = 30, restSeconds = 0, rounds = 4))

    assertEquals(4, plan.intervals.size)
    assertFalse(plan.intervals.any { it.type == IntervalType.REST })
  }

  @Test fun unsafeValuesAreClampedBeforePlanCreation() {
    val plan = planner.createPlan(IntervalWorkoutConfig(jumpSeconds = 1, restSeconds = 999, rounds = 100))

    assertEquals(IntervalWorkoutConfig.MAX_ROUNDS, plan.intervals.count { it.type == IntervalType.WORK })
    assertEquals(IntervalWorkoutConfig.MIN_JUMP_SECONDS, plan.intervals.first().durationSeconds)
    assertEquals(IntervalWorkoutConfig.MAX_REST_SECONDS, plan.intervals[1].durationSeconds)
  }

  @Test fun configurationReportsActiveAndTotalDuration() {
    val configuration = IntervalWorkoutConfig(jumpSeconds = 45, restSeconds = 20, rounds = 6)

    assertEquals(270, configuration.activeSeconds)
    assertEquals(370, configuration.totalSeconds)
  }
}
