package com.example.jump.core.domain

import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import javax.inject.Inject

class IntervalWorkoutPlanner @Inject constructor() {
  fun createPlan(configuration: IntervalWorkoutConfig): WorkoutPlan {
    val config = configuration.normalized()
    val intervals = buildList {
      repeat(config.rounds) { round ->
        add(WorkoutInterval(IntervalType.WORK, config.jumpSeconds))
        if (round < config.rounds - 1 && config.restSeconds > 0) {
          add(WorkoutInterval(IntervalType.REST, config.restSeconds))
        }
      }
    }
    return WorkoutPlan(
      id = "custom-${config.jumpSeconds}-${config.restSeconds}-${config.rounds}",
      title = "Custom intervals",
      subtitle = "${config.rounds} ${if (config.rounds == 1) "round" else "rounds"} • ${config.jumpSeconds}s jump • ${config.restSeconds}s rest",
      kind = WorkoutKind.CUSTOM,
      intervals = intervals,
    )
  }
}
