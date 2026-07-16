package com.example.jump.core.workout

import com.example.jump.core.data.repository.WorkoutRepository
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutCoordinatorTest {
  @Test fun cameraJumpsUseTheSharedWorkoutMetricsPipeline() {
    val coordinator = WorkoutCoordinator(FakeWorkoutRepository())
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
    val coordinator = WorkoutCoordinator(FakeWorkoutRepository())
    coordinator.prepare(WorkoutPlan("camera-test", "Camera test", "", WorkoutKind.QUICK), CountingMode.CAMERA, now = 0)

    coordinator.pause()
    coordinator.pause()

    assertEquals(SessionPhase.PAUSED, coordinator.state.value.phase)
    assertEquals(SessionPhase.PREPARING, coordinator.state.value.phaseBeforePause)
  }

  @Test fun configuredWorkoutTransitionsThroughEveryJumpAndRestRound() {
    val coordinator = WorkoutCoordinator(FakeWorkoutRepository())
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
}

private class FakeWorkoutRepository : WorkoutRepository {
  override val sessions: Flow<List<WorkoutSession>> = MutableStateFlow(emptyList())
  override suspend fun save(session: WorkoutSession): Long = 1
  override suspend fun session(id: Long): WorkoutSession? = null
  override suspend fun correctJumps(id: Long, jumps: Int) = Unit
}
