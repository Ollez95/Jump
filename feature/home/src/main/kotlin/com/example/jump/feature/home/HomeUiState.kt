package com.example.jump.feature.home

import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.domain.GamificationRules
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.GamificationState
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.QuestId
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutSession

enum class HomeLoadState { LOADING, READY, ERROR }

data class HomeUiState(
  val loadState: HomeLoadState = HomeLoadState.LOADING,
  val profile: UserProfile = UserProfile(),
  val sessions: List<WorkoutSession> = emptyList(),
  val dailyPlan: WorkoutPlan? = null,
  val active: ActiveWorkoutState = ActiveWorkoutState(),
  val countingMode: CountingMode = CountingMode.MOTION,
  val intervalWorkoutConfig: IntervalWorkoutConfig = IntervalWorkoutConfig(),
  val intervalCalorieEstimate: WorkoutCalorieEstimate = WorkoutCalorieEstimate(),
  val completedSessionsThisWeek: Int = 0,
  val personalBestJumps: Int = 0,
  val gamification: GamificationState = GamificationState(),
)

data class HomeQuestUi(
  val id: QuestId,
  val progress: Int,
  val target: Int,
  val completed: Boolean,
)

fun GamificationState.primaryDailyQuest(): HomeQuestUi {
  val quest = dailyQuests.firstOrNull { it.id == QuestId.DAILY_JUMPS }
    ?: dailyQuests.firstOrNull { it.id == QuestId.DAILY_WORKOUT }
  val id = quest?.id ?: QuestId.DAILY_JUMPS
  return HomeQuestUi(
    id = id,
    progress = quest?.progress?.coerceAtMost(id.target) ?: 0,
    target = id.target,
    completed = quest?.completed == true,
  )
}

internal fun completedSessionsInCurrentWeek(
  sessions: List<WorkoutSession>,
  nowEpochMillis: Long,
  timeZoneId: String,
): Int {
  val currentWeek = GamificationRules.PeriodKeys.from(nowEpochMillis, timeZoneId).weekly
  return sessions.count { session ->
    session.status == SessionStatus.COMPLETED &&
      GamificationRules.PeriodKeys.from(session.startedAtEpochMillis, timeZoneId).weekly == currentWeek
  }
}
