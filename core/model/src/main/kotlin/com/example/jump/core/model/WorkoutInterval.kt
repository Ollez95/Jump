package com.example.jump.core.model

data class WorkoutInterval(
  val type: IntervalType,
  val durationSeconds: Int,
  val targetCadence: Int? = null,
)
