package com.example.jump.core.domain

import com.example.jump.core.model.AchievementId
import com.example.jump.core.model.QuestId
import com.google.common.truth.Truth.assertThat
import java.util.GregorianCalendar
import java.util.TimeZone
import org.junit.Test

class GamificationRulesTest {
  @Test fun correctedJumpsRatherThanDetectedJumpsDriveXpAndQuestProgress() {
    val output = evaluate(correctedJumps = 500)

    assertThat(output.baseXp).isEqualTo(100)
    assertThat(output.questValues.single { it.id == QuestId.DAILY_JUMPS }.progress).isEqualTo(500)
    assertThat(output.completedQuests).contains(QuestId.DAILY_JUMPS)
  }

  @Test fun consecutiveLocalDaysGrowStreakAndMissedDayResetsIt() {
    val first = evaluate(at = epoch(2026, 7, 10, 23, 55))
    val second = evaluate(
      at = epoch(2026, 7, 11, 0, 5),
      profile = GamificationRules.Profile(currentStreak = 1, longestStreak = 1, lastActivityDay = first.activityDay),
    )
    val missed = evaluate(
      at = epoch(2026, 7, 13, 0, 5),
      profile = GamificationRules.Profile(currentStreak = 2, longestStreak = 2, lastActivityDay = second.activityDay),
    )

    assertThat(second.currentStreak).isEqualTo(2)
    assertThat(missed.currentStreak).isEqualTo(1)
    assertThat(missed.longestStreak).isEqualTo(2)
  }

  @Test fun timezoneControlsCalendarBoundaryDeterministically() {
    val instant = epoch(2026, 7, 10, 23, 30)

    val utc = GamificationRules.PeriodKeys.from(instant, "UTC")
    val madrid = GamificationRules.PeriodKeys.from(instant, "Europe/Madrid")

    assertThat(utc.daily).isEqualTo("2026-07-10")
    assertThat(madrid.daily).isEqualTo("2026-07-11")
    assertThat(madrid.localDay).isEqualTo(utc.localDay + 1)
  }

  @Test fun dailyAndWeeklyQuestsOnlyAwardXpWhenCrossingTheirTargets() {
    val at = epoch(2026, 7, 13, 12, 0)
    val keys = GamificationRules.PeriodKeys.from(at, "UTC")
    val current = mapOf(
      QuestId.DAILY_WORKOUT to GamificationRules.QuestValue(QuestId.DAILY_WORKOUT, keys.daily, 1, true),
      QuestId.DAILY_JUMPS to GamificationRules.QuestValue(QuestId.DAILY_JUMPS, keys.daily, 450, false),
      QuestId.WEEKLY_WORKOUTS to GamificationRules.QuestValue(QuestId.WEEKLY_WORKOUTS, keys.weekly, 2, false),
      QuestId.WEEKLY_JUMPS to GamificationRules.QuestValue(QuestId.WEEKLY_JUMPS, keys.weekly, 2_950, false),
    )

    val output = evaluate(correctedJumps = 50, at = at, quests = current)

    assertThat(output.completedQuests).containsExactly(
      QuestId.DAILY_JUMPS,
      QuestId.WEEKLY_WORKOUTS,
      QuestId.WEEKLY_JUMPS,
    )
    assertThat(output.questXp).isEqualTo(300)
  }

  @Test fun xpThresholdAdvancesLevelAndAchievementUnlocksAreOneTime() {
    val output = evaluate(
      correctedJumps = 1_000,
      completedWorkoutCount = 10,
      correctedJumpsTotal = 5_000,
      profile = GamificationRules.Profile(totalXp = 450, currentStreak = 2, longestStreak = 2, lastActivityDay = 20_000),
      unlocked = setOf(AchievementId.FIRST_WORKOUT),
      at = epoch(2026, 7, 11, 12, 0),
    )

    assertThat(output.levelBefore).isEqualTo(1)
    assertThat(output.levelAfter).isGreaterThan(1)
    assertThat(output.unlockedAchievements).doesNotContain(AchievementId.FIRST_WORKOUT)
    assertThat(output.unlockedAchievements).containsAtLeast(
      AchievementId.ONE_THOUSAND_TOTAL_JUMPS,
      AchievementId.ONE_THOUSAND_JUMP_WORKOUT,
      AchievementId.TEN_WORKOUTS,
    )
  }

  private fun evaluate(
    correctedJumps: Int = 10,
    completedWorkoutCount: Int = 1,
    correctedJumpsTotal: Int = correctedJumps,
    profile: GamificationRules.Profile = GamificationRules.Profile(),
    quests: Map<QuestId, GamificationRules.QuestValue> = emptyMap(),
    unlocked: Set<AchievementId> = emptySet(),
    at: Long = epoch(2026, 7, 10, 12, 0),
  ) = GamificationRules.evaluate(
    GamificationRules.Input(
      correctedJumps = correctedJumps,
      activeMillis = 0,
      completedWorkoutCount = completedWorkoutCount,
      correctedJumpsTotal = correctedJumpsTotal,
      profile = profile,
      currentQuestValues = quests,
      unlockedAchievements = unlocked,
      awardedAtEpochMillis = at,
      timeZoneId = "UTC",
    ),
  )

  private fun epoch(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
    GregorianCalendar(TimeZone.getTimeZone("UTC")).apply {
      clear()
      set(year, month - 1, day, hour, minute)
    }.timeInMillis
}
