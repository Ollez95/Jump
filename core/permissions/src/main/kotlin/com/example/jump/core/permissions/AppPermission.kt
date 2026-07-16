package com.example.jump.core.permissions

import android.Manifest

internal enum class AppPermission(val manifestPermission: String) {
  CAMERA(Manifest.permission.CAMERA),
  PHYSICAL_ACTIVITY(Manifest.permission.ACTIVITY_RECOGNITION),
  NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS),
}
