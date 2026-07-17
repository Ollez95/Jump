package com.example.jump.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.jumpDataStore by preferencesDataStore(name = "jump_preferences")

@Singleton
class JumpPreferencesDataSource @Inject constructor(@ApplicationContext context: Context) {
  private val dataStore = context.jumpDataStore
  private val safeData = dataStore.data.catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }

  val profile: Flow<UserProfile> = safeData.map { values ->
    UserProfile(
      onboardingComplete = values[JumpPreferencesKeys.onboarding] ?: false,
      experienceLevel = values[JumpPreferencesKeys.experience]?.let { runCatching { ExperienceLevel.valueOf(it) }.getOrNull() } ?: ExperienceLevel.BEGINNER,
      trainingGoal = values[JumpPreferencesKeys.goal]?.let { runCatching { TrainingGoal.valueOf(it) }.getOrNull() } ?: TrainingGoal.CONSISTENCY,
      sessionsPerWeek = values[JumpPreferencesKeys.frequency] ?: 3,
    )
  }

  val welcomeComplete: Flow<Boolean> = safeData.map { values ->
    values[JumpPreferencesKeys.welcomeComplete] ?: false
  }

  val cues: Flow<CuePreferences> = safeData.map { values ->
    CuePreferences(
      values[JumpPreferencesKeys.voice] ?: true,
      values[JumpPreferencesKeys.tones] ?: true,
      values[JumpPreferencesKeys.vibration] ?: true,
    )
  }

  val countingMode: Flow<CountingMode> = safeData.map { values ->
    values[JumpPreferencesKeys.countingMode]
      ?.let { runCatching { CountingMode.valueOf(it) }.getOrNull() }
      ?: CountingMode.MOTION
  }

  val intervalWorkoutConfig: Flow<IntervalWorkoutConfig> = safeData.map { values ->
    IntervalWorkoutConfig(
      jumpSeconds = values[JumpPreferencesKeys.customJumpSeconds] ?: IntervalWorkoutConfig.DEFAULT_JUMP_SECONDS,
      restSeconds = values[JumpPreferencesKeys.customRestSeconds] ?: IntervalWorkoutConfig.DEFAULT_REST_SECONDS,
      rounds = values[JumpPreferencesKeys.customRounds] ?: IntervalWorkoutConfig.DEFAULT_ROUNDS,
    ).normalized()
  }

  suspend fun saveProfile(profile: UserProfile) {
    dataStore.edit { values ->
      values[JumpPreferencesKeys.onboarding] = true
      values[JumpPreferencesKeys.experience] = profile.experienceLevel.name
      values[JumpPreferencesKeys.goal] = profile.trainingGoal.name
      values[JumpPreferencesKeys.frequency] = profile.sessionsPerWeek.coerceIn(2, 6)
    }
  }

  suspend fun setWelcomeComplete() {
    dataStore.edit { values -> values[JumpPreferencesKeys.welcomeComplete] = true }
  }

  suspend fun setCues(cues: CuePreferences) {
    dataStore.edit { values ->
      values[JumpPreferencesKeys.voice] = cues.voiceEnabled
      values[JumpPreferencesKeys.tones] = cues.tonesEnabled
      values[JumpPreferencesKeys.vibration] = cues.vibrationEnabled
    }
  }

  suspend fun setCountingMode(mode: CountingMode) {
    dataStore.edit { values -> values[JumpPreferencesKeys.countingMode] = mode.name }
  }

  suspend fun setIntervalWorkoutConfig(configuration: IntervalWorkoutConfig) {
    val config = configuration.normalized()
    dataStore.edit { values ->
      values[JumpPreferencesKeys.customJumpSeconds] = config.jumpSeconds
      values[JumpPreferencesKeys.customRestSeconds] = config.restSeconds
      values[JumpPreferencesKeys.customRounds] = config.rounds
    }
  }
}
