package com.example.jump.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.WorkoutCalorieEstimator
import com.example.jump.core.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
  private val workouts: WorkoutRepository,
  calorieEstimator: WorkoutCalorieEstimator,
) : ViewModel() {
  val uiState = workouts.sessions
    .map { sessions ->
      HistoryUiState.Loaded(
        sessions.map { HistorySessionUiModel(it, calorieEstimator.estimate(it)) },
      ) as HistoryUiState
    }
    .catch { emit(HistoryUiState.Error) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState.Loading)

  fun deleteSession(id: Long) {
    viewModelScope.launch { workouts.deleteSession(id) }
  }
}
