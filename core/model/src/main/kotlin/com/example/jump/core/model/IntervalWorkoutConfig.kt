package com.example.jump.core.model

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
