package com.example.jump.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
  @Query("SELECT * FROM workout_sessions ORDER BY startedAtEpochMillis DESC")
  fun observeSessions(): Flow<List<WorkoutSessionEntity>>

  @Query("SELECT * FROM workout_sessions WHERE id = :id")
  suspend fun session(id: Long): WorkoutSessionEntity?

  @Query("SELECT * FROM workout_intervals WHERE sessionId = :sessionId ORDER BY position")
  suspend fun intervals(sessionId: Long): List<WorkoutIntervalEntity>

  @Insert
  suspend fun insertSession(session: WorkoutSessionEntity): Long

  @Insert
  suspend fun insertIntervals(intervals: List<WorkoutIntervalEntity>)

  @Query("UPDATE workout_sessions SET correctedJumps = :jumps WHERE id = :id")
  suspend fun updateCorrectedJumps(id: Long, jumps: Int)

  @Query("DELETE FROM workout_sessions WHERE id = :id")
  suspend fun deleteSession(id: Long)
}
