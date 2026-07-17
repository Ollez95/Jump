package com.example.jump.feature.workout

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WorkoutPresentationTest {
  private val plan = WorkoutPlan(
    id = "test",
    title = "Test",
    subtitle = "",
    kind = WorkoutKind.CUSTOM,
    intervals = listOf(
      WorkoutInterval(IntervalType.WORK, 60),
      WorkoutInterval(IntervalType.REST, 15),
      WorkoutInterval(IntervalType.WORK, 60),
    ),
  )

  @Test
  fun intervalProgressUsesCurrentIntervalDuration() {
    val state = ActiveWorkoutState(
      plan = plan,
      phase = SessionPhase.RESTING,
      intervalIndex = 1,
      intervalRemainingMillis = 7_500,
    )

    assertThat(state.intervalProgress()).isWithin(0.001f).of(0.5f)
  }

  @Test
  fun roundInfoDoesNotAdvanceDuringRest() {
    val state = ActiveWorkoutState(plan = plan, phase = SessionPhase.RESTING, intervalIndex = 1)

    assertThat(state.roundInfo()).isEqualTo(RoundInfo(current = 1, total = 2))
  }

  @Test
  fun calorieRangeUsesACompactAccessibleValue() {
    assertThat(WorkoutCalorieEstimate(42, 64).displayRange()).isEqualTo("42–64")
  }
}
