package com.example.jump.core.domain

import com.example.jump.core.model.WorkoutSession
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class WorkoutProgressAnalyzer @Inject constructor(
  private val calorieEstimator: WorkoutCalorieEstimator,
) {
  fun analyze(
    sessions: List<WorkoutSession>,
    nowEpochMillis: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getDefault(),
  ): WorkoutProgressReport {
    val trainingSessions = sessions.filter {
      it.startedAtEpochMillis <= nowEpochMillis && (it.activeMillis > 0 || it.metrics.correctedJumps > 0)
    }
    val sessionsByDay = trainingSessions.groupBy { startOfDay(it.startedAtEpochMillis, timeZone) }
    val today = startOfDay(nowEpochMillis, timeZone)
    val currentWeekStart = startOfWeek(today, timeZone)
    val calendarStart = addDays(currentWeekStart, -(CALENDAR_WEEKS - 1) * DAYS_PER_WEEK, timeZone)

    val calendarDays = List(CALENDAR_WEEKS * DAYS_PER_WEEK) { offset ->
      val dayStart = addDays(calendarStart, offset, timeZone)
      val daySessions = sessionsByDay[dayStart].orEmpty()
      TrainingDay(
        dayStartEpochMillis = dayStart,
        sessionCount = daySessions.size,
        jumps = daySessions.sumOf { it.metrics.correctedJumps },
        activeMillis = daySessions.sumOf { it.activeMillis },
        isFuture = dayStart > today,
      )
    }

    val weeklyStart = addDays(currentWeekStart, -(CHART_WEEKS - 1) * DAYS_PER_WEEK, timeZone)
    val weeklyActivity = List(CHART_WEEKS) { weekIndex ->
      val weekStart = addDays(weeklyStart, weekIndex * DAYS_PER_WEEK, timeZone)
      val weekEnd = addDays(weekStart, DAYS_PER_WEEK, timeZone)
      val weekSessions = trainingSessions.filter {
        it.startedAtEpochMillis >= weekStart && it.startedAtEpochMillis < weekEnd
      }
      TrainingWeek(
        weekStartEpochMillis = weekStart,
        sessionCount = weekSessions.size,
        activeMillis = weekSessions.sumOf { it.activeMillis },
        jumps = weekSessions.sumOf { it.metrics.correctedJumps },
      )
    }

    val activeDays = sessionsByDay.keys.sorted()
    val totalActiveMillis = trainingSessions.sumOf { it.activeMillis }
    return WorkoutProgressReport(
      calendarDays = calendarDays,
      weeklyActivity = weeklyActivity,
      totalSessions = trainingSessions.size,
      totalTrainingDays = activeDays.size,
      totalJumps = trainingSessions.sumOf { it.metrics.correctedJumps },
      totalActiveMillis = totalActiveMillis,
      currentStreakDays = currentStreak(activeDays, today, timeZone),
      longestStreakDays = longestStreak(activeDays, timeZone),
      calorieEstimate = calorieEstimator.estimateActiveTime((totalActiveMillis / 1_000L).toInt()),
    )
  }

  private fun currentStreak(activeDays: List<Long>, today: Long, timeZone: TimeZone): Int {
    if (activeDays.isEmpty()) return 0
    val lastActiveDay = activeDays.last()
    val yesterday = addDays(today, -1, timeZone)
    if (lastActiveDay != today && lastActiveDay != yesterday) return 0

    var streak = 1
    var expected = addDays(lastActiveDay, -1, timeZone)
    for (day in activeDays.asReversed().drop(1)) {
      if (day != expected) break
      streak++
      expected = addDays(expected, -1, timeZone)
    }
    return streak
  }

  private fun longestStreak(activeDays: List<Long>, timeZone: TimeZone): Int {
    if (activeDays.isEmpty()) return 0
    var longest = 1
    var current = 1
    activeDays.zipWithNext().forEach { (previous, next) ->
      if (next == addDays(previous, 1, timeZone)) {
        current++
        longest = maxOf(longest, current)
      } else {
        current = 1
      }
    }
    return longest
  }

  private fun startOfDay(epochMillis: Long, timeZone: TimeZone): Long =
    Calendar.getInstance(timeZone).apply {
      timeInMillis = epochMillis
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.timeInMillis

  private fun startOfWeek(dayStart: Long, timeZone: TimeZone): Long =
    Calendar.getInstance(timeZone).apply {
      timeInMillis = dayStart
      val daysSinceMonday = (get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + DAYS_PER_WEEK) % DAYS_PER_WEEK
      add(Calendar.DAY_OF_YEAR, -daysSinceMonday)
    }.timeInMillis

  private fun addDays(dayStart: Long, days: Int, timeZone: TimeZone): Long =
    Calendar.getInstance(timeZone).apply {
      timeInMillis = dayStart
      add(Calendar.DAY_OF_YEAR, days)
    }.timeInMillis

  private companion object {
    const val DAYS_PER_WEEK = 7
    const val CALENDAR_WEEKS = 13
    const val CHART_WEEKS = 8
  }
}
