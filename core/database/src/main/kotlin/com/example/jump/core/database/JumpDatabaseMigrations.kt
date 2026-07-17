package com.example.jump.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object JumpDatabaseMigrations {
  val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL(
        """CREATE TABLE IF NOT EXISTS `gamification_profile` (`id` INTEGER NOT NULL, `totalXp` INTEGER NOT NULL, `currentStreak` INTEGER NOT NULL, `longestStreak` INTEGER NOT NULL, `lastActivityDay` INTEGER, PRIMARY KEY(`id`))""",
      )
      db.execSQL(
        """CREATE TABLE IF NOT EXISTS `workout_reward_ledger` (`sessionId` INTEGER NOT NULL, `baseXp` INTEGER NOT NULL, `questXp` INTEGER NOT NULL, `achievementXp` INTEGER NOT NULL, `totalAwardedXp` INTEGER NOT NULL, `totalXpAfter` INTEGER NOT NULL, `levelBefore` INTEGER NOT NULL, `levelAfter` INTEGER NOT NULL, `currentStreak` INTEGER NOT NULL, `completedQuestIds` TEXT NOT NULL, `unlockedAchievementIds` TEXT NOT NULL, `awardedAtEpochMillis` INTEGER NOT NULL, PRIMARY KEY(`sessionId`), FOREIGN KEY(`sessionId`) REFERENCES `workout_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)""",
      )
      db.execSQL(
        """CREATE TABLE IF NOT EXISTS `quest_progress` (`questId` TEXT NOT NULL, `periodKey` TEXT NOT NULL, `progress` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `completedAtEpochMillis` INTEGER, PRIMARY KEY(`questId`, `periodKey`))""",
      )
      db.execSQL(
        """CREATE TABLE IF NOT EXISTS `achievement_unlocks` (`achievementId` TEXT NOT NULL, `sessionId` INTEGER NOT NULL, `unlockedAtEpochMillis` INTEGER NOT NULL, PRIMARY KEY(`achievementId`))""",
      )
    }
  }
}
