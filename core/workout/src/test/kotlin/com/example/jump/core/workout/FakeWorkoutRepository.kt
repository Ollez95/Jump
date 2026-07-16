package com.example.jump.core.workout

import com.example.jump.core.domain.repository.WorkoutRepository
import com.example.jump.core.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeWorkoutRepository : WorkoutRepository {
  override val sessions: Flow<List<WorkoutSession>> = MutableStateFlow(emptyList())
  val savedSessions = mutableListOf<WorkoutSession>()

  override suspend fun save(session: WorkoutSession): Long {
    savedSessions += session
    return savedSessions.size.toLong()
  }

  override suspend fun session(id: Long): WorkoutSession? = null

  override suspend fun correctJumps(id: Long, jumps: Int) = Unit

  override suspend fun deleteSession(id: Long) = Unit
}
