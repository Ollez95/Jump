package com.example.jump.core.domain

import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveWorkoutPlannerTest {
  private val planner = AdaptiveWorkoutPlanner()

  @Test fun beginnerPlanAlternatesWorkAndRest() {
    val plan = planner.createDailyPlan(UserProfile(true, ExperienceLevel.BEGINNER, TrainingGoal.CONSISTENCY, 3), emptyList())
    assertEquals(6, plan.intervals.count { it.type == IntervalType.WORK })
    assertEquals(5, plan.intervals.count { it.type == IntervalType.REST })
  }

  @Test fun successfulHistoryProgressesByNoMoreThanTenPercent() {
    val profile = UserProfile(true, ExperienceLevel.REGULAR, TrainingGoal.CONSISTENCY, 3)
    val baseline = planner.createDailyPlan(profile, emptyList()).intervals.first().durationSeconds
    val sessions = List(3) { session(it.toLong(), SessionStatus.COMPLETED) }
    val progressed = planner.createDailyPlan(profile, sessions).intervals.first().durationSeconds
    assertTrue(progressed > baseline)
    assertTrue(progressed <= (baseline * 1.1).toInt() + 1)
  }

  private fun session(id: Long, status: SessionStatus) = WorkoutSession(
    id, "daily", "Daily", WorkoutKind.DAILY, id, 60_000, 45_000,
    JumpMetrics(100, 100, 120, 140, 100), status,
  )
}
