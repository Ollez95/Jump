package com.example.jump.core.model

data class UserProfile(
  val onboardingComplete: Boolean = false,
  val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
  val trainingGoal: TrainingGoal = TrainingGoal.CONSISTENCY,
  val sessionsPerWeek: Int = 3,
)
