package com.example.jump.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object JumpPreferencesKeys {
  val onboarding = booleanPreferencesKey("onboarding_complete")
  val experience = stringPreferencesKey("experience")
  val goal = stringPreferencesKey("goal")
  val frequency = intPreferencesKey("frequency")
  val voice = booleanPreferencesKey("voice")
  val tones = booleanPreferencesKey("tones")
  val vibration = booleanPreferencesKey("vibration")
  val countingMode = stringPreferencesKey("counting_mode")
  val customJumpSeconds = intPreferencesKey("custom_jump_seconds")
  val customRestSeconds = intPreferencesKey("custom_rest_seconds")
  val customRounds = intPreferencesKey("custom_rounds")
}
