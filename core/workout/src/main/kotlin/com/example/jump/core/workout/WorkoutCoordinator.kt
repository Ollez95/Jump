package com.example.jump.core.workout

import com.example.jump.core.domain.repository.WorkoutRepository
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutSession
import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class WorkoutCoordinator @Inject constructor(private val repository: WorkoutRepository) {
  private val mutableState = MutableStateFlow(ActiveWorkoutState())
  val state: StateFlow<ActiveWorkoutState> = mutableState.asStateFlow()
  private val recentJumps = ArrayDeque<Long>()
  private val finishMutex = Mutex()
  private var lastTickMillis = 0L
  private var lastJumpMillis = 0L

  @Synchronized fun prepare(
    plan: WorkoutPlan,
    countingMode: CountingMode = CountingMode.MOTION,
    sensorAvailable: Boolean = true,
    now: Long = System.currentTimeMillis(),
  ) {
    recentJumps.clear(); lastTickMillis = now; lastJumpMillis = 0
    mutableState.value = ActiveWorkoutState(
      plan = plan, phase = SessionPhase.PREPARING, startedAtEpochMillis = now,
      intervalRemainingMillis = 3_000, calibrationRemainingMillis = 10_000, sensorAvailable = sensorAvailable,
      countingMode = countingMode,
    )
  }

  @Synchronized fun tick(now: Long = System.currentTimeMillis()): WorkoutTransition? {
    val old = mutableState.value
    if (!old.isRunning || old.phase == SessionPhase.PAUSED) { lastTickMillis = now; return null }
    val delta = (now - lastTickMillis).coerceIn(0, 1_000); lastTickMillis = now
    var next = old.copy(
      elapsedMillis = old.elapsedMillis + delta,
      activeMillis = old.activeMillis + if (old.phase == SessionPhase.ACTIVE) delta else 0,
      calibrationRemainingMillis = (old.calibrationRemainingMillis - if (old.phase == SessionPhase.ACTIVE) delta else 0).coerceAtLeast(0),
      intervalRemainingMillis = (old.intervalRemainingMillis - delta).coerceAtLeast(0),
    )
    if (lastJumpMillis > 0 && now - lastJumpMillis > 3_000 && next.currentStreak != 0) {
      next = next.copy(currentStreak = 0, currentPace = 0); recentJumps.clear()
    }
    var transition: WorkoutTransition? = null
    if (next.intervalRemainingMillis == 0L) when (next.phase) {
      SessionPhase.PREPARING -> {
        next = if (next.plan?.kind == WorkoutKind.QUICK) next.copy(phase = SessionPhase.ACTIVE, intervalRemainingMillis = Long.MAX_VALUE / 2) else applyInterval(next, 0)
        transition = WorkoutTransition(next.phase, next.intervalIndex)
      }
      SessionPhase.ACTIVE, SessionPhase.RESTING -> {
        val index = next.intervalIndex + 1
        if (index >= (next.plan?.intervals?.size ?: 0)) {
          next = next.copy(phase = SessionPhase.COMPLETED)
          transition = WorkoutTransition(SessionPhase.COMPLETED, index)
        } else {
          next = applyInterval(next, index)
          transition = WorkoutTransition(next.phase, index)
        }
      }
      else -> Unit
    }
    mutableState.value = next
    return transition
  }

  private fun applyInterval(state: ActiveWorkoutState, index: Int): ActiveWorkoutState {
    val interval = state.plan!!.intervals[index]
    return state.copy(
      phase = if (interval.type == IntervalType.WORK) SessionPhase.ACTIVE else SessionPhase.RESTING,
      intervalIndex = index, intervalRemainingMillis = interval.durationSeconds * 1_000L,
    )
  }

  @Synchronized fun registerJump(now: Long = System.currentTimeMillis()) {
    val old = mutableState.value
    if (old.phase != SessionPhase.ACTIVE) return
    if (lastJumpMillis > 0 && now - lastJumpMillis > 3_000) recentJumps.clear()
    lastJumpMillis = now; recentJumps.addLast(now)
    while (recentJumps.isNotEmpty() && now - recentJumps.first() > 10_000) recentJumps.removeFirst()
    val pace = if (recentJumps.size < 2) 0 else (((recentJumps.size - 1) * 60_000.0) / (recentJumps.last() - recentJumps.first()).coerceAtLeast(1)).toInt().coerceAtMost(300)
    val streak = old.currentStreak + 1
    mutableState.value = old.copy(
      detectedJumps = old.detectedJumps + 1, correctedJumps = old.correctedJumps + 1,
      currentPace = pace, bestPace = maxOf(old.bestPace, pace), currentStreak = streak,
      longestStreak = maxOf(old.longestStreak, streak),
    )
  }

  @Synchronized fun togglePause() {
    val old = mutableState.value
    mutableState.value = if (old.phase == SessionPhase.PAUSED) {
      lastTickMillis = System.currentTimeMillis(); old.copy(phase = old.phaseBeforePause)
    } else if (old.phase in setOf(SessionPhase.PREPARING, SessionPhase.ACTIVE, SessionPhase.RESTING)) old.copy(phase = SessionPhase.PAUSED, phaseBeforePause = old.phase) else old
  }

  @Synchronized fun pause() {
    val old = mutableState.value
    if (old.phase in setOf(SessionPhase.PREPARING, SessionPhase.ACTIVE, SessionPhase.RESTING)) {
      mutableState.value = old.copy(phase = SessionPhase.PAUSED, phaseBeforePause = old.phase)
    }
  }

  suspend fun finish(status: SessionStatus = SessionStatus.COMPLETED): Long? = finishMutex.withLock {
    val snapshot = mutableState.value
    snapshot.savedSessionId?.let { return@withLock it }
    val plan = snapshot.plan ?: return null
    if (snapshot.phase == SessionPhase.IDLE) return null
    val averagePace = if (snapshot.activeMillis == 0L) 0 else (snapshot.detectedJumps * 60_000L / snapshot.activeMillis).toInt()
    val id = repository.save(WorkoutSession(
      planId = plan.id, title = plan.title, kind = plan.kind, startedAtEpochMillis = snapshot.startedAtEpochMillis,
      durationMillis = snapshot.elapsedMillis, activeMillis = snapshot.activeMillis,
      metrics = JumpMetrics(snapshot.detectedJumps, snapshot.correctedJumps, averagePace, snapshot.bestPace, snapshot.longestStreak),
      status = status, intervals = plan.intervals,
    ))
    mutableState.value = snapshot.copy(phase = SessionPhase.COMPLETED, savedSessionId = id)
    id
  }

  suspend fun correctJumps(jumps: Int) {
    val value = mutableState.value; val corrected = jumps.coerceAtLeast(0)
    mutableState.value = value.copy(correctedJumps = corrected)
    value.savedSessionId?.let { repository.correctJumps(it, corrected) }
  }

  fun clear() { mutableState.value = ActiveWorkoutState(); recentJumps.clear() }
}
