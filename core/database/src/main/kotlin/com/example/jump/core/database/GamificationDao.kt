package com.example.jump.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
  @Query("SELECT * FROM gamification_profile WHERE id = 1")
  fun observeProfile(): Flow<GamificationProfileEntity?>

  @Query("SELECT * FROM gamification_profile WHERE id = 1")
  suspend fun profile(): GamificationProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertProfile(profile: GamificationProfileEntity)

  @Query("SELECT * FROM workout_reward_ledger WHERE sessionId = :sessionId")
  suspend fun reward(sessionId: Long): WorkoutRewardEntity?

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertReward(reward: WorkoutRewardEntity)

  @Query("SELECT * FROM quest_progress WHERE periodKey = :periodKey")
  suspend fun questProgress(periodKey: String): List<QuestProgressEntity>

  @Query("SELECT * FROM quest_progress")
  fun observeQuestProgress(): Flow<List<QuestProgressEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertQuestProgress(progress: List<QuestProgressEntity>)

  @Query("SELECT * FROM achievement_unlocks ORDER BY unlockedAtEpochMillis, achievementId")
  fun observeAchievements(): Flow<List<AchievementUnlockEntity>>

  @Query("SELECT * FROM achievement_unlocks")
  suspend fun achievements(): List<AchievementUnlockEntity>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAchievements(achievements: List<AchievementUnlockEntity>)
}
