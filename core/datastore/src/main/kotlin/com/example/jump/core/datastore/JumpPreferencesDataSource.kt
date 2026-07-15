package com.example.jump.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.ExperienceLevel
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
  private object Keys {
    val onboarding = booleanPreferencesKey("onboarding_complete")
    val experience = stringPreferencesKey("experience")
    val goal = stringPreferencesKey("goal")
    val frequency = intPreferencesKey("frequency")
    val voice = booleanPreferencesKey("voice")
    val tones = booleanPreferencesKey("tones")
    val vibration = booleanPreferencesKey("vibration")
  }

  private val dataStore = context.jumpDataStore
  private val safeData = dataStore.data.catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }

  val profile: Flow<UserProfile> = safeData.map { values ->
    UserProfile(
      onboardingComplete = values[Keys.onboarding] ?: false,
      experienceLevel = values[Keys.experience]?.let { runCatching { ExperienceLevel.valueOf(it) }.getOrNull() } ?: ExperienceLevel.BEGINNER,
      trainingGoal = values[Keys.goal]?.let { runCatching { TrainingGoal.valueOf(it) }.getOrNull() } ?: TrainingGoal.CONSISTENCY,
      sessionsPerWeek = values[Keys.frequency] ?: 3,
    )
  }

  val cues: Flow<CuePreferences> = safeData.map { values ->
    CuePreferences(values[Keys.voice] ?: true, values[Keys.tones] ?: true, values[Keys.vibration] ?: true)
  }

  suspend fun saveProfile(profile: UserProfile) {
    dataStore.edit { values ->
      values[Keys.onboarding] = true
      values[Keys.experience] = profile.experienceLevel.name
      values[Keys.goal] = profile.trainingGoal.name
      values[Keys.frequency] = profile.sessionsPerWeek.coerceIn(2, 6)
    }
  }

  suspend fun setCues(cues: CuePreferences) {
    dataStore.edit { values ->
      values[Keys.voice] = cues.voiceEnabled
      values[Keys.tones] = cues.tonesEnabled
      values[Keys.vibration] = cues.vibrationEnabled
    }
  }

  suspend fun resetOnboarding() { dataStore.edit { it[Keys.onboarding] = false } }
}
