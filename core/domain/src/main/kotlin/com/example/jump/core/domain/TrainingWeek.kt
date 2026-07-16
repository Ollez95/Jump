package com.example.jump.core.domain

data class TrainingWeek(
  val weekStartEpochMillis: Long,
  val sessionCount: Int,
  val activeMillis: Long,
  val jumps: Int,
)
