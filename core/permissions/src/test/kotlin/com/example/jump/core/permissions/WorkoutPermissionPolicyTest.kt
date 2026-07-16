package com.example.jump.core.permissions

import android.os.Build
import com.example.jump.core.model.CountingMode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WorkoutPermissionPolicyTest {
  @Test
  fun cameraRequiresCameraPermissionOnEverySupportedSdk() {
    val request = WorkoutPermissionPolicy.requestFor(CountingMode.CAMERA, Build.VERSION_CODES.N)

    assertEquals(AppPermission.CAMERA, request.countingPermission)
    assertEquals(listOf(AppPermission.CAMERA), request.permissions)
  }

  @Test
  fun motionDoesNotRequirePhysicalActivityBeforeAndroidTen() {
    val request = WorkoutPermissionPolicy.requestFor(CountingMode.MOTION, Build.VERSION_CODES.P)

    assertEquals(null, request.countingPermission)
    assertEquals(emptyList<AppPermission>(), request.permissions)
  }

  @Test
  fun motionRequiresPhysicalActivityFromAndroidTen() {
    val request = WorkoutPermissionPolicy.requestFor(CountingMode.MOTION, Build.VERSION_CODES.Q)

    assertEquals(AppPermission.PHYSICAL_ACTIVITY, request.countingPermission)
    assertEquals(listOf(AppPermission.PHYSICAL_ACTIVITY), request.permissions)
  }

  @Test
  fun notificationsAreRequestedButDoNotBlockCounting() {
    val request = WorkoutPermissionPolicy.requestFor(
      CountingMode.CAMERA,
      Build.VERSION_CODES.TIRAMISU,
    )

    assertEquals(AppPermission.CAMERA, request.countingPermission)
    assertEquals(
      listOf(AppPermission.CAMERA, AppPermission.NOTIFICATIONS),
      request.permissions,
    )
  }
}
