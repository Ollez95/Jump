package com.example.jump.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.domain.WorkoutCalorieEstimator
import com.example.jump.core.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
  private val workouts: WorkoutRepository,
  private val calorieEstimator: WorkoutCalorieEstimator,
) : ViewModel() {
  private val mutableState = MutableStateFlow(SessionDetailUiState())
  val state = mutableState.asStateFlow()

  fun load(id: Long) {
    viewModelScope.launch {
      val session = workouts.session(id)
      mutableState.value = SessionDetailUiState(
        session = session,
        calorieEstimate = session?.let(calorieEstimator::estimate) ?: WorkoutCalorieEstimate(),
      )
    }
  }

  fun deleteSession(onDeleted: () -> Unit) {
    val id = mutableState.value.session?.id ?: return
    viewModelScope.launch {
      workouts.deleteSession(id)
      onDeleted()
    }
  }
}
