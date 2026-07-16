package com.example.jump.core.domain

data class WorkoutProgressReport(
  val calendarDays: List<TrainingDay>,
  val weeklyActivity: List<TrainingWeek>,
  val totalSessions: Int,
  val totalTrainingDays: Int,
  val totalJumps: Int,
  val totalActiveMillis: Long,
  val currentStreakDays: Int,
  val longestStreakDays: Int,
  val calorieEstimate: WorkoutCalorieEstimate,
)
