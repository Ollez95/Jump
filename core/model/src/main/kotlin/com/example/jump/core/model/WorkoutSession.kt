package com.example.jump.core.model

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
