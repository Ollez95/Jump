package com.example.jump.core.domain

import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutSession
import javax.inject.Inject
import kotlin.math.roundToInt

class AdaptiveWorkoutPlanner @Inject constructor() {
  fun createDailyPlan(profile: UserProfile, recentSessions: List<WorkoutSession>): WorkoutPlan {
    val base = when (profile.experienceLevel) {
      ExperienceLevel.BEGINNER -> AdaptiveWorkoutBasePlan(6, 30, 30, 90)
      ExperienceLevel.REGULAR -> AdaptiveWorkoutBasePlan(8, 45, 20, 115)
      ExperienceLevel.ADVANCED -> AdaptiveWorkoutBasePlan(10, 60, 15, 140)
    }
    val adjusted = when (profile.trainingGoal) {
      TrainingGoal.CONSISTENCY -> base.copy(rest = maxOf(base.rest, 20))
      TrainingGoal.ENDURANCE -> base.copy(work = (base.work * 1.15).roundToInt(), cadence = base.cadence - 5)
      TrainingGoal.SPEED -> base.copy(work = maxOf(25, (base.work * .8).roundToInt()), cadence = base.cadence + 20)
    }
    val relevant = recentSessions.filter { it.kind == WorkoutKind.DAILY }.take(3)
    val completionRate = if (relevant.isEmpty()) .75 else relevant.count { it.status == SessionStatus.COMPLETED }.toDouble() / relevant.size
    val progression = when {
      relevant.size < 2 -> 1.0
      completionRate >= .8 -> 1.1
      completionRate < .5 -> .9
      else -> 1.0
    }
    val workSeconds = (adjusted.work * progression).roundToInt().coerceAtLeast(20)
    val intervals = buildList {
      repeat(adjusted.rounds) { round ->
        add(WorkoutInterval(IntervalType.WORK, workSeconds, adjusted.cadence))
        if (round < adjusted.rounds - 1) add(WorkoutInterval(IntervalType.REST, adjusted.rest))
      }
    }
    val minutes = (intervals.sumOf { it.durationSeconds } + 59) / 60
    return WorkoutPlan(
      id = "daily-${profile.experienceLevel.name.lowercase()}-${profile.trainingGoal.name.lowercase()}",
      title = when (profile.trainingGoal) {
        TrainingGoal.CONSISTENCY -> "Find your rhythm"
        TrainingGoal.ENDURANCE -> "Stay in motion"
        TrainingGoal.SPEED -> "Quick feet"
      },
      subtitle = "$minutes min • ${adjusted.rounds} rounds • ${adjusted.cadence} target pace",
      kind = WorkoutKind.DAILY,
      intervals = intervals,
    )
  }

  fun quickPlan() = WorkoutPlan("quick-jump", "Quick jump", "No timer. Just find your flow.", WorkoutKind.QUICK)
}
