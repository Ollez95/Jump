package com.example.jump.core.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "gamification_profile")
data class GamificationProfileEntity(
  @androidx.room.PrimaryKey val id: Int = SINGLETON_ID,
  val totalXp: Int,
  val currentStreak: Int,
  val longestStreak: Int,
  val lastActivityDay: Long?,
) {
  companion object { const val SINGLETON_ID = 1 }
}

@Entity(
  tableName = "workout_reward_ledger",
  foreignKeys = [ForeignKey(
    entity = WorkoutSessionEntity::class,
    parentColumns = ["id"],
    childColumns = ["sessionId"],
    onDelete = ForeignKey.CASCADE,
  )],
)
data class WorkoutRewardEntity(
  @androidx.room.PrimaryKey val sessionId: Long,
  val baseXp: Int,
  val questXp: Int,
  val achievementXp: Int,
  val totalAwardedXp: Int,
  val totalXpAfter: Int,
  val levelBefore: Int,
  val levelAfter: Int,
  val currentStreak: Int,
  val completedQuestIds: String,
  val unlockedAchievementIds: String,
  val awardedAtEpochMillis: Long,
)

@Entity(tableName = "quest_progress", primaryKeys = ["questId", "periodKey"])
data class QuestProgressEntity(
  val questId: String,
  val periodKey: String,
  val progress: Int,
  val completed: Boolean,
  val completedAtEpochMillis: Long?,
)

@Entity(
  tableName = "achievement_unlocks",
)
data class AchievementUnlockEntity(
  @androidx.room.PrimaryKey val achievementId: String,
  val sessionId: Long,
  val unlockedAtEpochMillis: Long,
)
