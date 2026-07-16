package com.example.jump.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.WorkoutCalorieEstimator
import com.example.jump.core.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
  private val workouts: WorkoutRepository,
  calorieEstimator: WorkoutCalorieEstimator,
) : ViewModel() {
  val sessions = workouts.sessions
    .map { sessions ->
      sessions.map { HistorySessionUiModel(it, calorieEstimator.estimate(it)) }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  fun deleteSession(id: Long) {
    viewModelScope.launch { workouts.deleteSession(id) }
  }
}
