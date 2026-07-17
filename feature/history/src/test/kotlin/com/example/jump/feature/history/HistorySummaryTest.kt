package com.example.jump.feature.history

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutSession
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HistorySummaryTest {
  @Test
  fun `summary combines corrected jumps and calorie ranges`() {
    val sessions = listOf(
      session(id = 1, jumps = 240, minimumCalories = 20, maximumCalories = 32),
      session(id = 2, jumps = 360, minimumCalories = 28, maximumCalories = 44),
    )

    assertThat(historySummary(sessions)).isEqualTo(
      HistorySummary(workouts = 2, totalJumps = 600, minimumCalories = 48, maximumCalories = 76),
    )
  }

  @Test
  fun `empty summary is zeroed`() {
    assertThat(historySummary(emptyList())).isEqualTo(HistorySummary(0, 0, 0, 0))
  }

  private fun session(
    id: Long,
    jumps: Int,
    minimumCalories: Int,
    maximumCalories: Int,
  ) = HistorySessionUiModel(
    session = WorkoutSession(
      id = id,
      planId = "test",
      title = "Workout",
      kind = WorkoutKind.DAILY,
      startedAtEpochMillis = 1L,
      durationMillis = 60_000L,
      activeMillis = 50_000L,
      metrics = JumpMetrics(correctedJumps = jumps),
      status = SessionStatus.COMPLETED,
    ),
    calorieEstimate = WorkoutCalorieEstimate(
      minimumCalories = minimumCalories,
      maximumCalories = maximumCalories,
    ),
  )
}
