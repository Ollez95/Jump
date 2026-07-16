package com.example.jump.core.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.jump.core.model.CountingMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidWorkoutPermissionManager @Inject constructor(
  @ApplicationContext private val context: Context,
) : WorkoutPermissionManager {
  override fun missingPermissions(countingMode: CountingMode): List<String> =
    WorkoutPermissionPolicy.requestFor(countingMode, Build.VERSION.SDK_INT)
      .permissions
      .filterNot(::isGranted)
      .map(AppPermission::manifestPermission)

  override fun hasCountingPermission(countingMode: CountingMode): Boolean =
    WorkoutPermissionPolicy.requestFor(countingMode, Build.VERSION.SDK_INT)
      .countingPermission
      ?.let(::isGranted)
      ?: true

  private fun isGranted(permission: AppPermission): Boolean =
    ContextCompat.checkSelfPermission(
      context,
      permission.manifestPermission,
    ) == PackageManager.PERMISSION_GRANTED
}
