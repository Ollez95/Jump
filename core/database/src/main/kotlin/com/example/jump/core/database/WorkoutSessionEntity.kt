package com.example.jump.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val planId: String,
  val title: String,
  val kind: String,
  val startedAtEpochMillis: Long,
  val durationMillis: Long,
  val activeMillis: Long,
  val detectedJumps: Int,
  val correctedJumps: Int,
  val averagePace: Int,
  val bestPace: Int,
  val longestStreak: Int,
  val status: String,
)
