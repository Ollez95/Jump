package com.example.jump.core.permissions

import android.os.Build
import com.example.jump.core.model.CountingMode

internal object WorkoutPermissionPolicy {
  fun requestFor(
    countingMode: CountingMode,
    sdkInt: Int,
  ): WorkoutPermissionRequest {
    val countingPermission = when (countingMode) {
      CountingMode.CAMERA -> AppPermission.CAMERA
      CountingMode.MOTION -> AppPermission.PHYSICAL_ACTIVITY.takeIf {
        sdkInt >= Build.VERSION_CODES.Q
      }
    }
    val permissions = buildList {
      countingPermission?.let(::add)
      if (sdkInt >= Build.VERSION_CODES.TIRAMISU) add(AppPermission.NOTIFICATIONS)
    }
    return WorkoutPermissionRequest(
      permissions = permissions,
      countingPermission = countingPermission,
    )
  }
}
