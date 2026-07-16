package com.example.jump.core.data.repository

import androidx.room.withTransaction
import com.example.jump.core.database.JumpDatabase
import com.example.jump.core.database.WorkoutDao
import com.example.jump.core.database.WorkoutIntervalEntity
import com.example.jump.core.domain.repository.WorkoutRepository
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class DefaultWorkoutRepository @Inject constructor(
  private val database: JumpDatabase,
  private val dao: WorkoutDao,
) : WorkoutRepository {
  override val sessions: Flow<List<WorkoutSession>> =
    dao.observeSessions().map { rows -> rows.map { it.toDomain() } }

  override suspend fun save(session: WorkoutSession): Long = database.withTransaction {
    val id = dao.insertSession(session.toEntity())
    dao.insertIntervals(
      session.intervals.mapIndexed { index, interval ->
        WorkoutIntervalEntity(
          sessionId = id,
          position = index,
          type = interval.type.name,
          durationSeconds = interval.durationSeconds,
          targetCadence = interval.targetCadence,
        )
      },
    )
    id
  }

  override suspend fun session(id: Long): WorkoutSession? {
    val row = dao.session(id) ?: return null
    val intervals = dao.intervals(id).map {
      WorkoutInterval(IntervalType.valueOf(it.type), it.durationSeconds, it.targetCadence)
    }
    return row.toDomain().copy(intervals = intervals)
  }

  override suspend fun correctJumps(id: Long, jumps: Int) =
    dao.updateCorrectedJumps(id, jumps.coerceAtLeast(0))

  override suspend fun deleteSession(id: Long) {
    dao.deleteSession(id)
  }
}
