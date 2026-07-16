package com.example.jump.core.permissions

import com.example.jump.core.model.CountingMode

interface WorkoutPermissionManager {
  fun missingPermissions(countingMode: CountingMode): List<String>
  fun hasCountingPermission(countingMode: CountingMode): Boolean
}
