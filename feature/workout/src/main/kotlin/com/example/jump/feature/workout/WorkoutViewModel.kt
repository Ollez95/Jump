package com.example.jump.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.workout.WorkoutController
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutViewModel @Inject constructor(
  coordinator: WorkoutCoordinator,
  private val controller: WorkoutController,
) : ViewModel() {
  val state = coordinator.state
  private val workoutCoordinator = coordinator

  fun togglePause() = controller.togglePause()

  fun pauseCamera() = controller.pause()

  fun finish() = controller.stop()

  fun correct(jumps: Int) = viewModelScope.launch {
    workoutCoordinator.correctJumps(jumps)
  }

  fun registerCameraJump() = workoutCoordinator.registerJump()

  fun clear() = workoutCoordinator.clear()
}
