package com.example.jump.core.workout

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object WorkoutCoroutinesModule {
  @Provides
  @Singleton
  @WorkoutDispatcher
  fun providesWorkoutDispatcher(): CoroutineDispatcher = Dispatchers.Default

  @Provides
  @Singleton
  @WorkoutApplicationScope
  fun providesWorkoutApplicationScope(
    @WorkoutDispatcher dispatcher: CoroutineDispatcher,
  ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
