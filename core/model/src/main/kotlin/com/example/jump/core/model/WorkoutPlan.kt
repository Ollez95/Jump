package com.example.jump.core.model

data class WorkoutPlan(
  val id: String,
  val title: String,
  val subtitle: String,
  val kind: WorkoutKind,
  val intervals: List<WorkoutInterval> = emptyList(),
) {
  val durationSeconds: Int get() = intervals.sumOf { it.durationSeconds }
}
