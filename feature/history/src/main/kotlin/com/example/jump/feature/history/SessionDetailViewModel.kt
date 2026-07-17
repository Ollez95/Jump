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
import kotlinx.coroutines.CancellationException

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
  private val workouts: WorkoutRepository,
  private val calorieEstimator: WorkoutCalorieEstimator,
) : ViewModel() {
  private val mutableState = MutableStateFlow(SessionDetailUiState())
  val state = mutableState.asStateFlow()

  fun load(id: Long) {
    viewModelScope.launch {
      mutableState.value = SessionDetailUiState(status = SessionDetailStatus.LOADING)
      mutableState.value = try {
        val session = workouts.session(id)
        if (session == null) {
          SessionDetailUiState(status = SessionDetailStatus.NOT_FOUND)
        } else {
          SessionDetailUiState(
            status = SessionDetailStatus.LOADED,
            session = session,
            calorieEstimate = calorieEstimator.estimate(session),
          )
        }
      } catch (cancellation: CancellationException) {
        throw cancellation
      } catch (_: Exception) {
        SessionDetailUiState(status = SessionDetailStatus.ERROR)
      }
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
