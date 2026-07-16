package com.example.jump.core.data.repository

import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
  val profile: Flow<UserProfile>
  val cues: Flow<CuePreferences>
  val countingMode: Flow<CountingMode>
  suspend fun saveProfile(profile: UserProfile)
  suspend fun setCuePreferences(cues: CuePreferences)
  suspend fun setCountingMode(mode: CountingMode)
  suspend fun resetOnboarding()
}

interface WorkoutRepository {
  val sessions: Flow<List<WorkoutSession>>
  suspend fun save(session: WorkoutSession): Long
  suspend fun session(id: Long): WorkoutSession?
  suspend fun correctJumps(id: Long, jumps: Int)
}
