package com.example.jump.core.model

data class JumpMetrics(
  val detectedJumps: Int = 0,
  val correctedJumps: Int = 0,
  val averagePace: Int = 0,
  val bestPace: Int = 0,
  val longestStreak: Int = 0,
)
