package com.example.jump.core.domain.repository

import com.example.jump.core.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
  val sessions: Flow<List<WorkoutSession>>

  suspend fun save(session: WorkoutSession): Long
  suspend fun session(id: Long): WorkoutSession?
  suspend fun correctJumps(id: Long, jumps: Int)
  suspend fun deleteSession(id: Long)
}
