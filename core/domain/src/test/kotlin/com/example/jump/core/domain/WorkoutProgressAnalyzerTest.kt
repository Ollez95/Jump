package com.example.jump.core.domain

import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutSession
import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutProgressAnalyzerTest {
  private val utc = TimeZone.getTimeZone("UTC")
  private val analyzer = WorkoutProgressAnalyzer(WorkoutCalorieEstimator())

  @Test fun reportBuildsCalendarWeeklyChartAndTotals() {
    val now = day(2026, Calendar.JULY, 16, 12)
    val sessions = listOf(
      session(day(2026, Calendar.JULY, 14, 8), activeMinutes = 10, jumps = 700),
      session(day(2026, Calendar.JULY, 14, 18), activeMinutes = 5, jumps = 300),
      session(day(2026, Calendar.JULY, 15, 9), activeMinutes = 8, jumps = 500),
    )

    val report = analyzer.analyze(sessions, now, utc)

    assertEquals(91, report.calendarDays.size)
    assertEquals(8, report.weeklyActivity.size)
    assertEquals(3, report.totalSessions)
    assertEquals(2, report.totalTrainingDays)
    assertEquals(1_500, report.totalJumps)
    assertEquals(23 * 60_000L, report.totalActiveMillis)
    assertEquals(2, report.currentStreakDays)
    assertEquals(2, report.longestStreakDays)
    assertEquals(2, report.calendarDays.first { it.dayStartEpochMillis == day(2026, Calendar.JULY, 14) }.sessionCount)
    assertTrue(report.calendarDays.any { it.isFuture })
  }

  @Test fun streaksHandleGapsAndContinueFromYesterday() {
    val now = day(2026, Calendar.JANUARY, 5, 12)
    val sessions = listOf(
      session(day(2025, Calendar.DECEMBER, 29), 5, 100),
      session(day(2025, Calendar.DECEMBER, 30), 5, 100),
      session(day(2025, Calendar.DECEMBER, 31), 5, 100),
      session(day(2026, Calendar.JANUARY, 3), 5, 100),
      session(day(2026, Calendar.JANUARY, 4), 5, 100),
    )

    val report = analyzer.analyze(sessions, now, utc)

    assertEquals(2, report.currentStreakDays)
    assertEquals(3, report.longestStreakDays)
  }

  @Test fun emptyAndFutureSessionsDoNotCountAsTraining() {
    val now = day(2026, Calendar.JULY, 16, 12)
    val sessions = listOf(
      session(day(2026, Calendar.JULY, 15), activeMinutes = 0, jumps = 0),
      session(day(2026, Calendar.JULY, 17), activeMinutes = 10, jumps = 500),
    )

    val report = analyzer.analyze(sessions, now, utc)

    assertEquals(0, report.totalSessions)
    assertEquals(0, report.currentStreakDays)
    assertEquals(0, report.longestStreakDays)
    assertFalse(report.calendarDays.any { it.trained })
  }

  private fun session(startedAt: Long, activeMinutes: Int, jumps: Int) = WorkoutSession(
    planId = "test",
    title = "Test workout",
    kind = WorkoutKind.CUSTOM,
    startedAtEpochMillis = startedAt,
    durationMillis = activeMinutes * 60_000L,
    activeMillis = activeMinutes * 60_000L,
    metrics = JumpMetrics(correctedJumps = jumps),
    status = SessionStatus.COMPLETED,
  )

  private fun day(year: Int, month: Int, day: Int, hour: Int = 0): Long =
    Calendar.getInstance(utc).apply {
      clear()
      set(year, month, day, hour, 0, 0)
    }.timeInMillis
}
