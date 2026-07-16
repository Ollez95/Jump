package com.example.jump.core.workout

import com.example.jump.core.model.SessionPhase

data class WorkoutTransition(
  val phase: SessionPhase,
  val intervalIndex: Int,
)
