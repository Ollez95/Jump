package com.example.jump.core.domain

import com.example.jump.core.model.AchievementId
import com.example.jump.core.model.QuestCadence
import com.example.jump.core.model.QuestId
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.max

object GamificationRules {
  const val XP_PER_LEVEL = 500

  data class Profile(
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDay: Long? = null,
  )

  data class QuestValue(val id: QuestId, val periodKey: String, val progress: Int, val completed: Boolean)

  data class Input(
    val correctedJumps: Int,
    val activeMillis: Long,
    val completedWorkoutCount: Int,
    val correctedJumpsTotal: Int,
    val profile: Profile,
    val currentQuestValues: Map<QuestId, QuestValue>,
    val unlockedAchievements: Set<AchievementId>,
    val awardedAtEpochMillis: Long,
    val timeZoneId: String,
  )

  data class Output(
    val baseXp: Int,
    val questXp: Int,
    val achievementXp: Int,
    val totalAwardedXp: Int,
    val totalXpAfter: Int,
    val levelBefore: Int,
    val levelAfter: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val activityDay: Long,
    val questValues: List<QuestValue>,
    val completedQuests: Set<QuestId>,
    val unlockedAchievements: Set<AchievementId>,
  )

  fun evaluate(input: Input): Output {
    val time = PeriodKeys.from(input.awardedAtEpochMillis, input.timeZoneId)
    val streak = streakFor(input.profile, time.localDay)
    val questValues = QuestId.entries.map { quest ->
      val periodKey = if (quest.cadence == QuestCadence.DAILY) time.daily else time.weekly
      val prior = input.currentQuestValues[quest].takeIf { it?.periodKey == periodKey }
      val increment = when (quest) {
        QuestId.DAILY_WORKOUT, QuestId.WEEKLY_WORKOUTS -> 1
        QuestId.DAILY_JUMPS, QuestId.WEEKLY_JUMPS -> input.correctedJumps.coerceAtLeast(0)
      }
      QuestValue(
        id = quest,
        periodKey = periodKey,
        progress = ((prior?.progress ?: 0) + increment).coerceAtMost(quest.target),
        completed = (prior?.completed == true) || (prior?.progress ?: 0) + increment >= quest.target,
      )
    }
    val newlyCompletedQuests = questValues.filter { value ->
      value.completed && input.currentQuestValues[value.id]?.let { it.periodKey == value.periodKey && it.completed } != true
    }.mapTo(linkedSetOf()) { it.id }

    val eligibleAchievements = buildSet {
      if (input.completedWorkoutCount >= 1) add(AchievementId.FIRST_WORKOUT)
      if (input.correctedJumpsTotal >= 1_000) add(AchievementId.ONE_THOUSAND_TOTAL_JUMPS)
      if (streak.current >= 3) add(AchievementId.THREE_DAY_STREAK)
      if (input.correctedJumps >= 1_000) add(AchievementId.ONE_THOUSAND_JUMP_WORKOUT)
      if (input.completedWorkoutCount >= 10) add(AchievementId.TEN_WORKOUTS)
    }
    val newAchievements = eligibleAchievements.minus(input.unlockedAchievements)
    val baseXp = 50 + input.correctedJumps.coerceAtLeast(0) / 10 +
      (input.activeMillis.coerceAtLeast(0) / 60_000L).toInt() * 2
    val questXp = newlyCompletedQuests.sumOf(QuestId::xpReward)
    val achievementXp = newAchievements.sumOf(AchievementId::xpReward)
    val awarded = baseXp + questXp + achievementXp
    val totalAfter = input.profile.totalXp + awarded
    return Output(
      baseXp = baseXp,
      questXp = questXp,
      achievementXp = achievementXp,
      totalAwardedXp = awarded,
      totalXpAfter = totalAfter,
      levelBefore = levelForXp(input.profile.totalXp),
      levelAfter = levelForXp(totalAfter),
      currentStreak = streak.current,
      longestStreak = streak.longest,
      activityDay = time.localDay,
      questValues = questValues,
      completedQuests = newlyCompletedQuests,
      unlockedAchievements = newAchievements,
    )
  }

  fun levelForXp(totalXp: Int): Int = totalXp.coerceAtLeast(0) / XP_PER_LEVEL + 1

  private fun streakFor(profile: Profile, activityDay: Long): Streak {
    val current = when (val last = profile.lastActivityDay) {
      null -> 1
      activityDay -> profile.currentStreak.coerceAtLeast(1)
      activityDay - 1 -> profile.currentStreak.coerceAtLeast(0) + 1
      else -> 1
    }
    return Streak(current, max(profile.longestStreak, current))
  }

  private data class Streak(val current: Int, val longest: Int)

  data class PeriodKeys(val daily: String, val weekly: String, val localDay: Long) {
    companion object {
      fun from(epochMillis: Long, timeZoneId: String): PeriodKeys {
        val local = GregorianCalendar(TimeZone.getTimeZone(timeZoneId)).apply {
          timeInMillis = epochMillis
          firstDayOfWeek = Calendar.MONDAY
          minimalDaysInFirstWeek = 4
        }
        val year = local.get(Calendar.YEAR)
        val month = local.get(Calendar.MONTH) + 1
        val day = local.get(Calendar.DAY_OF_MONTH)
        val weekYear = local.weekYear
        val week = local.get(Calendar.WEEK_OF_YEAR)
        val neutral = GregorianCalendar(TimeZone.getTimeZone("UTC")).apply {
          clear()
          set(year, month - 1, day)
        }
        return PeriodKeys(
          daily = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month, day),
          weekly = String.format(Locale.ROOT, "%04d-W%02d", weekYear, week),
          localDay = neutral.timeInMillis / 86_400_000L,
        )
      }
    }
  }
}
