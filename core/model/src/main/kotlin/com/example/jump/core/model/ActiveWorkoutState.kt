package com.example.jump.core.model

data class ActiveWorkoutState(
  val savedSessionId: Long? = null,
  val plan: WorkoutPlan? = null,
  val phase: SessionPhase = SessionPhase.IDLE,
  val phaseBeforePause: SessionPhase = SessionPhase.ACTIVE,
  val startedAtEpochMillis: Long = 0,
  val elapsedMillis: Long = 0,
  val activeMillis: Long = 0,
  val intervalIndex: Int = 0,
  val intervalRemainingMillis: Long = 0,
  val detectedJumps: Int = 0,
  val correctedJumps: Int = 0,
  val currentPace: Int = 0,
  val bestPace: Int = 0,
  val currentStreak: Int = 0,
  val longestStreak: Int = 0,
  val calibrationRemainingMillis: Long = 0,
  val sensorAvailable: Boolean = true,
  val countingMode: CountingMode = CountingMode.MOTION,
) {
  val isRunning: Boolean
    get() = phase in setOf(
      SessionPhase.PREPARING,
      SessionPhase.ACTIVE,
      SessionPhase.RESTING,
      SessionPhase.PAUSED,
    )
}
