package com.example.jump.feature.workoutsetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.IntervalWorkoutPlanner
import com.example.jump.core.domain.WorkoutCalorieEstimator
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutSetupViewModel @Inject constructor(
  private val preferences: UserPreferencesRepository,
  private val planner: IntervalWorkoutPlanner,
  private val calorieEstimator: WorkoutCalorieEstimator,
  coordinator: WorkoutCoordinator,
) : ViewModel() {
  private val configuration = MutableStateFlow(IntervalWorkoutConfig())

  val uiState = combine(configuration, preferences.countingMode, coordinator.state) { config, mode, active ->
    WorkoutSetupUiState(config, mode, active, calorieEstimator.estimate(config))
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutSetupUiState())

  init {
    viewModelScope.launch {
      preferences.intervalWorkoutConfig.collect { configuration.value = it }
    }
  }

  fun updateConfiguration(transform: (IntervalWorkoutConfig) -> IntervalWorkoutConfig) {
    val updated = transform(configuration.value).normalized()
    configuration.value = updated
    viewModelScope.launch { preferences.setIntervalWorkoutConfig(updated) }
  }

  fun updateCountingMode(mode: CountingMode) = viewModelScope.launch {
    preferences.setCountingMode(mode)
  }

  fun createPlan(): WorkoutPlan = planner.createPlan(configuration.value)
}
