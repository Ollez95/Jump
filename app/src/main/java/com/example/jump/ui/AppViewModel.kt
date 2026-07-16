package com.example.jump.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.permissions.WorkoutPermissionManager
import com.example.jump.core.workout.WorkoutController
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AppViewModel @Inject constructor(
  preferences: UserPreferencesRepository,
  coordinator: WorkoutCoordinator,
  private val controller: WorkoutController,
  private val permissions: WorkoutPermissionManager,
) : ViewModel() {
  private var pendingWorkoutStart: PendingWorkoutStart? = null
  val profile: StateFlow<UserProfile?> = preferences.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
  val active: StateFlow<ActiveWorkoutState> = coordinator.state

  fun prepareWorkoutStart(plan: WorkoutPlan, countingMode: CountingMode): List<String> {
    pendingWorkoutStart = PendingWorkoutStart(plan, countingMode)
    return permissions.missingPermissions(countingMode)
  }

  fun startPendingWorkoutIfPermitted(): Boolean {
    val pending = pendingWorkoutStart ?: return false
    if (!permissions.hasCountingPermission(pending.countingMode)) return false
    controller.start(pending.plan, pending.countingMode)
    pendingWorkoutStart = null
    return true
  }

  fun discardPendingWorkoutStart(): CountingMode? =
    pendingWorkoutStart?.countingMode.also { pendingWorkoutStart = null }
}
