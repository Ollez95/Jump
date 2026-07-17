package com.example.jump.core.workout

import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.domain.AwardWorkoutRewardsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutCoordinatorTest {
  private fun coordinator(
    workouts: FakeWorkoutRepository = FakeWorkoutRepository(),
    gamification: FakeGamificationRepository = FakeGamificationRepository(),
  ) = WorkoutCoordinator(workouts, AwardWorkoutRewardsUseCase(gamification))

  @Test fun cameraJumpsUseTheSharedWorkoutMetricsPipeline() {
    val coordinator = coordinator()
    val plan = WorkoutPlan("camera-test", "Camera test", "", WorkoutKind.QUICK)
    coordinator.prepare(plan, CountingMode.CAMERA, now = 0)
    coordinator.tick(1_000)
    coordinator.tick(2_000)
    coordinator.tick(3_000)

    coordinator.registerJump(3_150)

    assertEquals(SessionPhase.ACTIVE, coordinator.state.value.phase)
    assertEquals(CountingMode.CAMERA, coordinator.state.value.countingMode)
    assertEquals(1, coordinator.state.value.detectedJumps)
    assertEquals(1, coordinator.state.value.correctedJumps)
  }

  @Test fun pausingCameraWorkoutIsIdempotent() {
    val coordinator = coordinator()
    coordinator.prepare(WorkoutPlan("camera-test", "Camera test", "", WorkoutKind.QUICK), CountingMode.CAMERA, now = 0)

    coordinator.pause()
    coordinator.pause()

    assertEquals(SessionPhase.PAUSED, coordinator.state.value.phase)
    assertEquals(SessionPhase.PREPARING, coordinator.state.value.phaseBeforePause)
  }

  @Test fun configuredWorkoutTransitionsThroughEveryJumpAndRestRound() {
    val coordinator = coordinator()
    val plan = WorkoutPlan(
      id = "custom-test",
      title = "Custom intervals",
      subtitle = "",
      kind = WorkoutKind.CUSTOM,
      intervals = listOf(
        WorkoutInterval(IntervalType.WORK, 1),
        WorkoutInterval(IntervalType.REST, 1),
        WorkoutInterval(IntervalType.WORK, 1),
      ),
    )
    coordinator.prepare(plan, now = 0)

    coordinator.tick(1_000)
    coordinator.tick(2_000)
    coordinator.tick(3_000)
    assertEquals(SessionPhase.ACTIVE, coordinator.state.value.phase)
    assertEquals(0, coordinator.state.value.intervalIndex)

    coordinator.tick(4_000)
    assertEquals(SessionPhase.RESTING, coordinator.state.value.phase)
    assertEquals(1, coordinator.state.value.intervalIndex)

    coordinator.tick(5_000)
    assertEquals(SessionPhase.ACTIVE, coordinator.state.value.phase)
    assertEquals(2, coordinator.state.value.intervalIndex)

    coordinator.tick(6_000)
    assertEquals(SessionPhase.COMPLETED, coordinator.state.value.phase)
    assertEquals(2_000L, coordinator.state.value.activeMillis)
  }

  @Test fun concurrentFinishCallsPersistOnlyOneSession() = runTest {
    val repository = FakeWorkoutRepository()
    val coordinator = coordinator(workouts = repository)
    coordinator.prepare(WorkoutPlan("finish-test", "Finish test", "", WorkoutKind.QUICK), now = 0)

    val ids = listOf(
      async { coordinator.finish() },
      async { coordinator.finish() },
    ).awaitAll()

    assertEquals(listOf(1L, 1L), ids)
    assertEquals(1, repository.savedSessions.size)
    assertEquals(1L, coordinator.state.value.savedSessionId)
  }

  @Test fun completedWorkoutAwardsAfterSaveAndRewardFailureDoesNotDuplicateWorkout() = runTest {
    val workouts = FakeWorkoutRepository()
    val gamification = FakeGamificationRepository().apply { failure = IllegalStateException("offline") }
    val coordinator = coordinator(workouts, gamification)
    coordinator.prepare(WorkoutPlan("reward-test", "Reward test", "", WorkoutKind.QUICK), now = 0)

    val id = coordinator.finish()

    assertEquals(1L, id)
    assertEquals(1, workouts.savedSessions.size)
    assertEquals(1L, coordinator.state.value.savedSessionId)
  }
}
