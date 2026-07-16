package com.example.jump.core.model

enum class ExperienceLevel { BEGINNER, REGULAR, ADVANCED }
enum class TrainingGoal { CONSISTENCY, ENDURANCE, SPEED }
enum class IntervalType { WORK, REST }
enum class WorkoutKind { QUICK, DAILY, CUSTOM }
enum class CountingMode { MOTION, CAMERA }
enum class SessionStatus { COMPLETED, CANCELLED, INTERRUPTED }
enum class SessionPhase { IDLE, PREPARING, ACTIVE, RESTING, PAUSED, COMPLETED }

data class UserProfile(
  val onboardingComplete: Boolean = false,
  val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
  val trainingGoal: TrainingGoal = TrainingGoal.CONSISTENCY,
  val sessionsPerWeek: Int = 3,
)

data class CuePreferences(
  val voiceEnabled: Boolean = true,
  val tonesEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true,
)

data class WorkoutInterval(val type: IntervalType, val durationSeconds: Int, val targetCadence: Int? = null)

data class IntervalWorkoutConfig(
  val jumpSeconds: Int = DEFAULT_JUMP_SECONDS,
  val restSeconds: Int = DEFAULT_REST_SECONDS,
  val rounds: Int = DEFAULT_ROUNDS,
) {
  val activeSeconds: Int get() = jumpSeconds * rounds
  val totalSeconds: Int get() = activeSeconds + restSeconds * (rounds - 1).coerceAtLeast(0)

  fun normalized() = copy(
    jumpSeconds = jumpSeconds.coerceIn(MIN_JUMP_SECONDS, MAX_JUMP_SECONDS),
    restSeconds = restSeconds.coerceIn(MIN_REST_SECONDS, MAX_REST_SECONDS),
    rounds = rounds.coerceIn(MIN_ROUNDS, MAX_ROUNDS),
  )

  companion object {
    const val DEFAULT_JUMP_SECONDS = 30
    const val DEFAULT_REST_SECONDS = 20
    const val DEFAULT_ROUNDS = 6
    const val MIN_JUMP_SECONDS = 10
    const val MAX_JUMP_SECONDS = 300
    const val MIN_REST_SECONDS = 0
    const val MAX_REST_SECONDS = 180
    const val MIN_ROUNDS = 1
    const val MAX_ROUNDS = 60
  }
}

data class WorkoutPlan(
  val id: String,
  val title: String,
  val subtitle: String,
  val kind: WorkoutKind,
  val intervals: List<WorkoutInterval> = emptyList(),
) {
  val durationSeconds: Int get() = intervals.sumOf { it.durationSeconds }
}

data class JumpMetrics(
  val detectedJumps: Int = 0,
  val correctedJumps: Int = 0,
  val averagePace: Int = 0,
  val bestPace: Int = 0,
  val longestStreak: Int = 0,
)

data class WorkoutSession(
  val id: Long = 0,
  val planId: String,
  val title: String,
  val kind: WorkoutKind,
  val startedAtEpochMillis: Long,
  val durationMillis: Long,
  val activeMillis: Long,
  val metrics: JumpMetrics,
  val status: SessionStatus,
  val intervals: List<WorkoutInterval> = emptyList(),
)

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
  val isRunning: Boolean get() = phase in setOf(SessionPhase.PREPARING, SessionPhase.ACTIVE, SessionPhase.RESTING, SessionPhase.PAUSED)
}
