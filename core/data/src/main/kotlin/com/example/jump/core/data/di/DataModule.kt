package com.example.jump.core.data.di

import com.example.jump.core.data.repository.DefaultUserPreferencesRepository
import com.example.jump.core.data.repository.DefaultWorkoutRepository
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
  @Binds @Singleton abstract fun bindsUserPreferencesRepository(impl: DefaultUserPreferencesRepository): UserPreferencesRepository
  @Binds @Singleton abstract fun bindsWorkoutRepository(impl: DefaultWorkoutRepository): WorkoutRepository
}
