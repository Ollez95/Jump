package com.example.jump.feature.home

import com.example.jump.core.model.GamificationState
import com.example.jump.core.model.QuestId
import com.example.jump.core.model.QuestProgress
import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutSession
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

class HomeUiStateTest {
  @Test
  fun emptyRewardsExposeDeterministicDailyJumpQuest() {
    val quest = GamificationState().primaryDailyQuest()

    assertThat(quest.id).isEqualTo(QuestId.DAILY_JUMPS)
    assertThat(quest.progress).isEqualTo(0)
    assertThat(quest.target).isEqualTo(500)
    assertThat(quest.completed).isFalse()
  }

  @Test
  fun persistedDailyJumpQuestIsClampedForDisplay() {
    val quest = GamificationState(
      dailyQuests = listOf(
        QuestProgress(QuestId.DAILY_JUMPS, "2026-07-17", progress = 620, completed = true),
      ),
    ).primaryDailyQuest()

    assertThat(quest.progress).isEqualTo(500)
    assertThat(quest.completed).isTrue()
  }

  @Test
  fun weeklyCountUsesIsoWeekAndLocalTimezone() {
    val sundayUtc = session("2026-07-19T23:30:00Z")
    val previousMonday = session("2026-07-13T12:00:00Z")
    val now = Instant.parse("2026-07-20T12:00:00Z").toEpochMilli()

    assertThat(completedSessionsInCurrentWeek(listOf(sundayUtc, previousMonday), now, "UTC"))
      .isEqualTo(0)
    assertThat(completedSessionsInCurrentWeek(listOf(sundayUtc, previousMonday), now, "Europe/Madrid"))
      .isEqualTo(1)
  }

  private fun session(start: String) = WorkoutSession(
    planId = "test",
    title = "Test",
    kind = WorkoutKind.DAILY,
    startedAtEpochMillis = Instant.parse(start).toEpochMilli(),
    durationMillis = 60_000,
    activeMillis = 60_000,
    metrics = JumpMetrics(),
    status = SessionStatus.COMPLETED,
  )
}
