package com.example.jump.core.data.repository

import com.example.jump.core.datastore.JumpPreferencesDataSource
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserPreferencesRepository @Inject constructor(
  private val dataSource: JumpPreferencesDataSource,
) : UserPreferencesRepository {
  override val profile = dataSource.profile
  override val cues = dataSource.cues
  override val countingMode = dataSource.countingMode
  override val intervalWorkoutConfig = dataSource.intervalWorkoutConfig

  override suspend fun saveProfile(profile: UserProfile) {
    dataSource.saveProfile(profile)
  }

  override suspend fun setCuePreferences(cues: CuePreferences) {
    dataSource.setCues(cues)
  }

  override suspend fun setCountingMode(mode: CountingMode) {
    dataSource.setCountingMode(mode)
  }

  override suspend fun setIntervalWorkoutConfig(configuration: IntervalWorkoutConfig) {
    dataSource.setIntervalWorkoutConfig(configuration)
  }

  override suspend fun resetOnboarding() {
    dataSource.resetOnboarding()
  }
}
