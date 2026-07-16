package com.example.jump.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.AdaptiveWorkoutPlanner
import com.example.jump.core.domain.WorkoutCalorieEstimator
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.domain.repository.WorkoutRepository
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val preferences: UserPreferencesRepository,
  workouts: WorkoutRepository,
  coordinator: WorkoutCoordinator,
  private val planner: AdaptiveWorkoutPlanner,
  private val calorieEstimator: WorkoutCalorieEstimator,
) : ViewModel() {
  val uiState: StateFlow<HomeUiState> = combine(
    preferences.profile,
    workouts.sessions,
    coordinator.state,
    preferences.countingMode,
    preferences.intervalWorkoutConfig,
  ) { profile, sessions, active, countingMode, intervalConfig ->
    val weekStart = System.currentTimeMillis() - WEEK_MILLIS
    HomeUiState(
      profile = profile,
      sessions = sessions,
      dailyPlan = planner.createDailyPlan(profile, sessions),
      active = active,
      countingMode = countingMode,
      intervalWorkoutConfig = intervalConfig,
      intervalCalorieEstimate = calorieEstimator.estimate(intervalConfig),
      completedSessionsThisWeek = sessions.count {
        it.startedAtEpochMillis >= weekStart && it.status == SessionStatus.COMPLETED
      },
      personalBestJumps = sessions.maxOfOrNull { it.metrics.correctedJumps } ?: 0,
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

  val quickPlan: WorkoutPlan = planner.quickPlan()

  fun setCountingMode(mode: CountingMode) = viewModelScope.launch {
    preferences.setCountingMode(mode)
  }

  private companion object {
    const val WEEK_MILLIS = 7 * 24 * 60 * 60 * 1_000L
  }
}
