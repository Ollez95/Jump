package com.example.jump.core.model

enum class QuestCadence { DAILY, WEEKLY }

enum class QuestId(val cadence: QuestCadence, val target: Int, val xpReward: Int) {
  DAILY_WORKOUT(QuestCadence.DAILY, target = 1, xpReward = 25),
  DAILY_JUMPS(QuestCadence.DAILY, target = 500, xpReward = 50),
  WEEKLY_WORKOUTS(QuestCadence.WEEKLY, target = 3, xpReward = 100),
  WEEKLY_JUMPS(QuestCadence.WEEKLY, target = 3_000, xpReward = 150),
}

enum class AchievementId(val xpReward: Int) {
  FIRST_WORKOUT(100),
  ONE_THOUSAND_TOTAL_JUMPS(150),
  THREE_DAY_STREAK(200),
  ONE_THOUSAND_JUMP_WORKOUT(250),
  TEN_WORKOUTS(300),
}

data class QuestProgress(
  val id: QuestId,
  val periodKey: String,
  val progress: Int,
  val completed: Boolean,
)

data class AchievementUnlock(
  val id: AchievementId,
  val unlockedAtEpochMillis: Long,
  val workoutSessionId: Long,
)

data class GamificationState(
  val totalXp: Int = 0,
  val level: Int = 1,
  val xpInLevel: Int = 0,
  val xpToNextLevel: Int = 500,
  val currentStreak: Int = 0,
  val longestStreak: Int = 0,
  val dailyQuests: List<QuestProgress> = emptyList(),
  val weeklyQuests: List<QuestProgress> = emptyList(),
  val achievements: List<AchievementUnlock> = emptyList(),
)

data class WorkoutRewardResult(
  val workoutSessionId: Long,
  val baseXp: Int,
  val questXp: Int,
  val achievementXp: Int,
  val totalAwardedXp: Int,
  val totalXpAfter: Int,
  val levelBefore: Int,
  val levelAfter: Int,
  val currentStreak: Int,
  val completedQuests: Set<QuestId>,
  val unlockedAchievements: Set<AchievementId>,
  val awardedAtEpochMillis: Long,
  val alreadyAwarded: Boolean = false,
)
