package com.example.jump.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [
    WorkoutSessionEntity::class,
    WorkoutIntervalEntity::class,
    GamificationProfileEntity::class,
    WorkoutRewardEntity::class,
    QuestProgressEntity::class,
    AchievementUnlockEntity::class,
  ],
  version = 2,
  exportSchema = true,
)
abstract class JumpDatabase : RoomDatabase() {
  abstract fun workoutDao(): WorkoutDao
  abstract fun gamificationDao(): GamificationDao
}
