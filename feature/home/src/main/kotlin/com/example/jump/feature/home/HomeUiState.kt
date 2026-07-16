package com.example.jump.feature.home

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutSession

data class HomeUiState(
  val profile: UserProfile = UserProfile(),
  val sessions: List<WorkoutSession> = emptyList(),
  val dailyPlan: WorkoutPlan? = null,
  val active: ActiveWorkoutState = ActiveWorkoutState(),
  val countingMode: CountingMode = CountingMode.MOTION,
  val intervalWorkoutConfig: IntervalWorkoutConfig = IntervalWorkoutConfig(),
  val intervalCalorieEstimate: WorkoutCalorieEstimate = WorkoutCalorieEstimate(),
  val completedSessionsThisWeek: Int = 0,
  val personalBestJumps: Int = 0,
)
