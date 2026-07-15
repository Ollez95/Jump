package com.example.jump.core.workout

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutPlan
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutController @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val coordinator: WorkoutCoordinator,
) {
  fun start(plan: WorkoutPlan) {
    coordinator.prepare(plan)
    ContextCompat.startForegroundService(context, Intent(context, WorkoutService::class.java).setAction(WorkoutService.ACTION_START))
  }
  fun togglePause() = context.startService(Intent(context, WorkoutService::class.java).setAction(WorkoutService.ACTION_TOGGLE_PAUSE))
  fun stop() = context.startService(Intent(context, WorkoutService::class.java).setAction(WorkoutService.ACTION_STOP))
  fun cancel() = context.startService(Intent(context, WorkoutService::class.java).setAction(WorkoutService.ACTION_STOP).putExtra(WorkoutService.EXTRA_STATUS, SessionStatus.CANCELLED.name))
}
