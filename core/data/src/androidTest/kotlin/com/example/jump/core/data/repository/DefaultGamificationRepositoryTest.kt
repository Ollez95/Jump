package com.example.jump.core.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jump.core.database.JumpDatabase
import com.example.jump.core.database.WorkoutSessionEntity
import com.example.jump.core.domain.GamificationTimeProvider
import com.example.jump.core.model.AchievementId
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultGamificationRepositoryTest {
  private lateinit var database: JumpDatabase
  private lateinit var repository: DefaultGamificationRepository
  private val time = FakeGamificationTimeProvider(1_752_232_800_000L, "UTC")

  @Before fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, JumpDatabase::class.java).build()
    repository = DefaultGamificationRepository(database, database.workoutDao(), database.gamificationDao(), time)
  }

  @After fun tearDown() = database.close()

  @Test fun duplicateCompletionReturnsLedgerRewardWithoutAwardingXpTwice() = runTest {
    val sessionId = insertCompletedSession(correctedJumps = 200)

    val first = repository.awardForCompletedWorkout(sessionId)!!
    val duplicate = repository.awardForCompletedWorkout(sessionId)!!

    assertThat(first.alreadyAwarded).isFalse()
    assertThat(duplicate.alreadyAwarded).isTrue()
    assertThat(duplicate.totalAwardedXp).isEqualTo(first.totalAwardedXp)
    assertThat(database.gamificationDao().profile()!!.totalXp).isEqualTo(first.totalXpAfter)
  }

  @Test fun storedCorrectedJumpCountDrivesRewardAndAchievement() = runTest {
    val sessionId = insertCompletedSession(detectedJumps = 50, correctedJumps = 1_000)

    val reward = repository.awardForCompletedWorkout(sessionId)!!

    assertThat(reward.baseXp).isEqualTo(152)
    assertThat(reward.unlockedAchievements).contains(AchievementId.ONE_THOUSAND_JUMP_WORKOUT)
  }

  @Test fun nonCompletedSessionIsNeverRewarded() = runTest {
    val sessionId = insertCompletedSession(status = "INTERRUPTED")

    assertThat(repository.awardForCompletedWorkout(sessionId)).isNull()
    assertThat(database.gamificationDao().reward(sessionId)).isNull()
  }

  private suspend fun insertCompletedSession(
    detectedJumps: Int = 100,
    correctedJumps: Int = detectedJumps,
    status: String = "COMPLETED",
  ): Long = database.workoutDao().insertSession(
    WorkoutSessionEntity(
      planId = "test",
      title = "Test workout",
      kind = "QUICK",
      startedAtEpochMillis = time.nowEpochMillis(),
      durationMillis = 60_000,
      activeMillis = 60_000,
      detectedJumps = detectedJumps,
      correctedJumps = correctedJumps,
      averagePace = 100,
      bestPace = 120,
      longestStreak = correctedJumps,
      status = status,
    ),
  )
}

private class FakeGamificationTimeProvider(
  var now: Long,
  var zoneId: String,
) : GamificationTimeProvider {
  override fun nowEpochMillis(): Long = now
  override fun timeZoneId(): String = zoneId
}
