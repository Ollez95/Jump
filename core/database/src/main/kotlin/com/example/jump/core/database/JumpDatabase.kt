package com.example.jump.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val planId: String,
  val title: String,
  val kind: String,
  val startedAtEpochMillis: Long,
  val durationMillis: Long,
  val activeMillis: Long,
  val detectedJumps: Int,
  val correctedJumps: Int,
  val averagePace: Int,
  val bestPace: Int,
  val longestStreak: Int,
  val status: String,
)

@Entity(
  tableName = "workout_intervals",
  foreignKeys = [ForeignKey(
    entity = WorkoutSessionEntity::class,
    parentColumns = ["id"],
    childColumns = ["sessionId"],
    onDelete = ForeignKey.CASCADE,
  )],
  indices = [Index("sessionId")],
)
data class WorkoutIntervalEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: Long,
  val position: Int,
  val type: String,
  val durationSeconds: Int,
  val targetCadence: Int?,
)

@Dao
interface WorkoutDao {
  @Query("SELECT * FROM workout_sessions ORDER BY startedAtEpochMillis DESC") fun observeSessions(): Flow<List<WorkoutSessionEntity>>
  @Query("SELECT * FROM workout_sessions WHERE id = :id") suspend fun session(id: Long): WorkoutSessionEntity?
  @Query("SELECT * FROM workout_intervals WHERE sessionId = :sessionId ORDER BY position") suspend fun intervals(sessionId: Long): List<WorkoutIntervalEntity>
  @Insert suspend fun insertSession(session: WorkoutSessionEntity): Long
  @Insert suspend fun insertIntervals(intervals: List<WorkoutIntervalEntity>)
  @Query("UPDATE workout_sessions SET correctedJumps = :jumps WHERE id = :id") suspend fun updateCorrectedJumps(id: Long, jumps: Int)
}

@Database(entities = [WorkoutSessionEntity::class, WorkoutIntervalEntity::class], version = 1, exportSchema = true)
abstract class JumpDatabase : RoomDatabase() {
  abstract fun workoutDao(): WorkoutDao
}
