package com.example.jump.core.permissions

internal data class WorkoutPermissionRequest(
  val permissions: List<AppPermission>,
  val countingPermission: AppPermission?,
)
