package com.example.jump.core.data.repository

import androidx.room.withTransaction
import com.example.jump.core.database.AchievementUnlockEntity
import com.example.jump.core.database.GamificationDao
import com.example.jump.core.database.GamificationProfileEntity
import com.example.jump.core.database.JumpDatabase
import com.example.jump.core.database.QuestProgressEntity
import com.example.jump.core.database.WorkoutDao
import com.example.jump.core.database.WorkoutRewardEntity
import com.example.jump.core.domain.GamificationRules
import com.example.jump.core.domain.GamificationTimeProvider
import com.example.jump.core.domain.repository.GamificationRepository
import com.example.jump.core.model.AchievementId
import com.example.jump.core.model.AchievementUnlock
import com.example.jump.core.model.GamificationState
import com.example.jump.core.model.QuestCadence
import com.example.jump.core.model.QuestId
import com.example.jump.core.model.QuestProgress
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutRewardResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Singleton
class DefaultGamificationRepository @Inject constructor(
  private val database: JumpDatabase,
  private val workoutDao: WorkoutDao,
  private val gamificationDao: GamificationDao,
  private val timeProvider: GamificationTimeProvider,
) : GamificationRepository {

  override val state: Flow<GamificationState> = combine(
    gamificationDao.observeProfile(),
    gamificationDao.observeQuestProgress(),
    gamificationDao.observeAchievements(),
  ) { profile, quests, achievements ->
    val totalXp = profile?.totalXp ?: 0
    val level = GamificationRules.levelForXp(totalXp)
    val period = GamificationRules.PeriodKeys.from(timeProvider.nowEpochMillis(), timeProvider.timeZoneId())
    val currentRows = quests.filter { it.periodKey == period.daily || it.periodKey == period.weekly }
      .mapNotNull(QuestProgressEntity::toDomain).associateBy(QuestProgress::id)
    val currentQuests = QuestId.entries.map { id ->
      currentRows[id] ?: QuestProgress(
        id = id,
        periodKey = if (id.cadence == QuestCadence.DAILY) period.daily else period.weekly,
        progress = 0,
        completed = false,
      )
    }
    GamificationState(
      totalXp = totalXp,
      level = level,
      xpInLevel = totalXp % GamificationRules.XP_PER_LEVEL,
      xpToNextLevel = GamificationRules.XP_PER_LEVEL,
      currentStreak = profile?.currentStreak ?: 0,
      longestStreak = profile?.longestStreak ?: 0,
      dailyQuests = currentQuests.filter { it.id.cadence == QuestCadence.DAILY },
      weeklyQuests = currentQuests.filter { it.id.cadence == QuestCadence.WEEKLY },
      achievements = achievements.mapNotNull(AchievementUnlockEntity::toDomain),
    )
  }

  override suspend fun awardForCompletedWorkout(sessionId: Long): WorkoutRewardResult? =
    database.withTransaction {
      gamificationDao.reward(sessionId)?.let { return@withTransaction it.toDomain(alreadyAwarded = true) }
      val session = workoutDao.session(sessionId) ?: return@withTransaction null
      if (runCatching { SessionStatus.valueOf(session.status) }.getOrNull() != SessionStatus.COMPLETED) {
        return@withTransaction null
      }

      val awardedAt = timeProvider.nowEpochMillis()
      val timeZoneId = timeProvider.timeZoneId()
      val period = GamificationRules.PeriodKeys.from(awardedAt, timeZoneId)
      val questRows = (gamificationDao.questProgress(period.daily) + gamificationDao.questProgress(period.weekly))
      val currentQuests = questRows.mapNotNull { row ->
        val id = enumValueOrNull<QuestId>(row.questId) ?: return@mapNotNull null
        id to GamificationRules.QuestValue(id, row.periodKey, row.progress, row.completed)
      }.toMap()
      val profile = gamificationDao.profile()
      val unlocked = gamificationDao.achievements().mapNotNullTo(mutableSetOf()) {
        enumValueOrNull<AchievementId>(it.achievementId)
      }
      val output = GamificationRules.evaluate(
        GamificationRules.Input(
          correctedJumps = session.correctedJumps,
          activeMillis = session.activeMillis,
          completedWorkoutCount = workoutDao.completedSessionCount(),
          correctedJumpsTotal = workoutDao.completedCorrectedJumpsTotal(),
          profile = GamificationRules.Profile(
            totalXp = profile?.totalXp ?: 0,
            currentStreak = profile?.currentStreak ?: 0,
            longestStreak = profile?.longestStreak ?: 0,
            lastActivityDay = profile?.lastActivityDay,
          ),
          currentQuestValues = currentQuests,
          unlockedAchievements = unlocked,
          awardedAtEpochMillis = awardedAt,
          timeZoneId = timeZoneId,
        ),
      )
      gamificationDao.upsertProfile(
        GamificationProfileEntity(
          totalXp = output.totalXpAfter,
          currentStreak = output.currentStreak,
          longestStreak = output.longestStreak,
          lastActivityDay = output.activityDay,
        ),
      )
      gamificationDao.upsertQuestProgress(output.questValues.map { value ->
        QuestProgressEntity(
          questId = value.id.name,
          periodKey = value.periodKey,
          progress = value.progress,
          completed = value.completed,
          completedAtEpochMillis = if (value.id in output.completedQuests) awardedAt else
            questRows.firstOrNull { it.questId == value.id.name && it.periodKey == value.periodKey }?.completedAtEpochMillis,
        )
      })
      gamificationDao.insertAchievements(output.unlockedAchievements.map { achievement ->
        AchievementUnlockEntity(achievement.name, sessionId, awardedAt)
      })
      val reward = WorkoutRewardEntity(
        sessionId = sessionId,
        baseXp = output.baseXp,
        questXp = output.questXp,
        achievementXp = output.achievementXp,
        totalAwardedXp = output.totalAwardedXp,
        totalXpAfter = output.totalXpAfter,
        levelBefore = output.levelBefore,
        levelAfter = output.levelAfter,
        currentStreak = output.currentStreak,
        completedQuestIds = output.completedQuests.toStableNames(),
        unlockedAchievementIds = output.unlockedAchievements.toStableNames(),
        awardedAtEpochMillis = awardedAt,
      )
      gamificationDao.insertReward(reward)
      reward.toDomain(alreadyAwarded = false)
    }

  override suspend fun rewardForWorkout(sessionId: Long): WorkoutRewardResult? =
    gamificationDao.reward(sessionId)?.toDomain(alreadyAwarded = true)
}

private fun QuestProgressEntity.toDomain(): QuestProgress? {
  val id = enumValueOrNull<QuestId>(questId) ?: return null
  return QuestProgress(id, periodKey, progress, completed)
}

private fun AchievementUnlockEntity.toDomain(): AchievementUnlock? {
  val id = enumValueOrNull<AchievementId>(achievementId) ?: return null
  return AchievementUnlock(id, unlockedAtEpochMillis, sessionId)
}

private fun WorkoutRewardEntity.toDomain(alreadyAwarded: Boolean) = WorkoutRewardResult(
  workoutSessionId = sessionId,
  baseXp = baseXp,
  questXp = questXp,
  achievementXp = achievementXp,
  totalAwardedXp = totalAwardedXp,
  totalXpAfter = totalXpAfter,
  levelBefore = levelBefore,
  levelAfter = levelAfter,
  currentStreak = currentStreak,
  completedQuests = completedQuestIds.toEnums(),
  unlockedAchievements = unlockedAchievementIds.toEnums(),
  awardedAtEpochMillis = awardedAtEpochMillis,
  alreadyAwarded = alreadyAwarded,
)

private inline fun <reified T : Enum<T>> enumValueOrNull(name: String): T? =
  enumValues<T>().firstOrNull { it.name == name }

private fun Collection<Enum<*>>.toStableNames(): String = map(Enum<*>::name).sorted().joinToString(",")

private inline fun <reified T : Enum<T>> String.toEnums(): Set<T> =
  split(',').filter(String::isNotBlank).mapNotNull { enumValueOrNull<T>(it) }.toSet()
