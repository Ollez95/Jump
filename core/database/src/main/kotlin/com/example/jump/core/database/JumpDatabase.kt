package com.example.jump.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [WorkoutSessionEntity::class, WorkoutIntervalEntity::class],
  version = 1,
  exportSchema = true,
)
abstract class JumpDatabase : RoomDatabase() {
  abstract fun workoutDao(): WorkoutDao
}
