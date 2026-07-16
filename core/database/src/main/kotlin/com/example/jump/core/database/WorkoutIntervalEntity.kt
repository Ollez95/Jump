package com.example.jump.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "workout_intervals",
  foreignKeys = [
    ForeignKey(
      entity = WorkoutSessionEntity::class,
      parentColumns = ["id"],
      childColumns = ["sessionId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [Index("sessionId")],
)
data class WorkoutIntervalEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: Long,
  val position: Int,
  val type: String,
  val durationSeconds: Int,
  val targetCadence: Int?,
)
