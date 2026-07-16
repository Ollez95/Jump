package com.example.jump.core.permissions

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PermissionsModule {
  @Binds
  @Singleton
  abstract fun bindsWorkoutPermissionManager(
    implementation: AndroidWorkoutPermissionManager,
  ): WorkoutPermissionManager
}
