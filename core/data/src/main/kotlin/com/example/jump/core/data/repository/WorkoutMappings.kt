package com.example.jump.core.data.repository

import com.example.jump.core.database.WorkoutSessionEntity
import com.example.jump.core.model.JumpMetrics
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutSession

internal fun WorkoutSessionEntity.toDomain() = WorkoutSession(
  id,
  planId,
  title,
  WorkoutKind.valueOf(kind),
  startedAtEpochMillis,
  durationMillis,
  activeMillis,
  JumpMetrics(detectedJumps, correctedJumps, averagePace, bestPace, longestStreak),
  SessionStatus.valueOf(status),
)

internal fun WorkoutSession.toEntity() = WorkoutSessionEntity(
  id,
  planId,
  title,
  kind.name,
  startedAtEpochMillis,
  durationMillis,
  activeMillis,
  metrics.detectedJumps,
  metrics.correctedJumps,
  metrics.averagePace,
  metrics.bestPace,
  metrics.longestStreak,
  status.name,
)
