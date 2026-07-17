package com.example.jump.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.AwardWorkoutRewardsUseCase
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.domain.WorkoutCalorieEstimator
import com.example.jump.core.model.WorkoutRewardResult
import com.example.jump.core.workout.WorkoutController
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CompletionRewardUiState {
  data object NotReady : CompletionRewardUiState
  data object Loading : CompletionRewardUiState
  data object Unavailable : CompletionRewardUiState
  data class Ready(val reward: WorkoutRewardResult) : CompletionRewardUiState
}

@HiltViewModel
class WorkoutViewModel @Inject constructor(
  coordinator: WorkoutCoordinator,
  private val controller: WorkoutController,
  awardWorkoutRewards: AwardWorkoutRewardsUseCase,
  calorieEstimator: WorkoutCalorieEstimator,
) : ViewModel() {
  val state = coordinator.state
  private val workoutCoordinator = coordinator
  private val mutableCompletionReward = MutableStateFlow<CompletionRewardUiState>(CompletionRewardUiState.NotReady)
  val completionReward: StateFlow<CompletionRewardUiState> = mutableCompletionReward.asStateFlow()

  val calorieEstimate: StateFlow<WorkoutCalorieEstimate> = state
    .map { active ->
      calorieEstimator.estimateActiveTime((active.activeMillis / 1_000L).toInt())
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = WorkoutCalorieEstimate(),
    )

  init {
    viewModelScope.launch {
      state.map { it.savedSessionId }.distinctUntilChanged().collect { sessionId ->
        mutableCompletionReward.value = if (sessionId == null) {
          CompletionRewardUiState.NotReady
        } else {
          mutableCompletionReward.value = CompletionRewardUiState.Loading
          val reward = runCatching { awardWorkoutRewards(sessionId) }.getOrNull()
          reward?.let(CompletionRewardUiState::Ready) ?: CompletionRewardUiState.Unavailable
        }
      }
    }
  }

  fun togglePause() = controller.togglePause()

  fun pauseCamera() = controller.pause()

  fun finish() = controller.stop()

  fun correct(jumps: Int) = viewModelScope.launch {
    workoutCoordinator.correctJumps(jumps)
  }

  fun registerCameraJump() = workoutCoordinator.registerJump()

  fun clear() = workoutCoordinator.clear()
}
