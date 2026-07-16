package com.example.jump.core.domain

data class TrainingDay(
  val dayStartEpochMillis: Long,
  val sessionCount: Int = 0,
  val jumps: Int = 0,
  val activeMillis: Long = 0,
  val isFuture: Boolean = false,
) {
  val trained: Boolean get() = sessionCount > 0
}
