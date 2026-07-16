package com.example.jump.core.data.repository

import androidx.room.withTransaction
import com.example.jump.core.database.JumpDatabase
import com.example.jump.core.database.WorkoutDao
import com.example.jump.core.database.WorkoutIntervalEntity
import com.example.jump.core.database.WorkoutSessionEntity
import com.example.jump.core.datastore.JumpPreferencesDataSource
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class DefaultUserPreferencesRepository @Inject constructor(
  private val dataSource: JumpPreferencesDataSource,
) : UserPreferencesRepository {
  override val profile = dataSource.profile
  override val cues = dataSource.cues
  override val countingMode = dataSource.countingMode
  override val intervalWorkoutConfig = dataSource.intervalWorkoutConfig
  override suspend fun saveProfile(profile: UserProfile) { dataSource.saveProfile(profile) }
  override suspend fun setCuePreferences(cues: CuePreferences) { dataSource.setCues(cues) }
  override suspend fun setCountingMode(mode: CountingMode) { dataSource.setCountingMode(mode) }
  override suspend fun setIntervalWorkoutConfig(configuration: IntervalWorkoutConfig) {
    dataSource.setIntervalWorkoutConfig(configuration)
  }
  override suspend fun resetOnboarding() { dataSource.resetOnboarding() }
}

@Singleton
class DefaultWorkoutRepository @Inject constructor(
  private val database: JumpDatabase,
  private val dao: WorkoutDao,
) : WorkoutRepository {
  override val sessions: Flow<List<WorkoutSession>> = dao.observeSessions().map { rows -> rows.map { it.toDomain() } }

  override suspend fun save(session: WorkoutSession): Long = database.withTransaction {
    val id = dao.insertSession(session.toEntity())
    dao.insertIntervals(session.intervals.mapIndexed { index, interval ->
      WorkoutIntervalEntity(
        sessionId = id,
        position = index,
        type = interval.type.name,
        durationSeconds = interval.durationSeconds,
        targetCadence = interval.targetCadence,
      )
    })
    id
  }

  override suspend fun session(id: Long): WorkoutSession? {
    val row = dao.session(id) ?: return null
    val intervals = dao.intervals(id).map { WorkoutInterval(IntervalType.valueOf(it.type), it.durationSeconds, it.targetCadence) }
    return row.toDomain().copy(intervals = intervals)
  }

  override suspend fun correctJumps(id: Long, jumps: Int) = dao.updateCorrectedJumps(id, jumps.coerceAtLeast(0))
}

private fun WorkoutSessionEntity.toDomain() = WorkoutSession(
  id, planId, title, WorkoutKind.valueOf(kind), startedAtEpochMillis, durationMillis, activeMillis,
  JumpMetrics(detectedJumps, correctedJumps, averagePace, bestPace, longestStreak), SessionStatus.valueOf(status),
)

private fun WorkoutSession.toEntity() = WorkoutSessionEntity(
  id, planId, title, kind.name, startedAtEpochMillis, durationMillis, activeMillis,
  metrics.detectedJumps, metrics.correctedJumps, metrics.averagePace, metrics.bestPace, metrics.longestStreak, status.name,
)
