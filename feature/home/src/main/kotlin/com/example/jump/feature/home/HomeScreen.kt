package com.example.jump.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.branding.JumpBrandMark
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.feedback.JumpEmptyState
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.indicator.JumpBadge
import com.example.jump.core.designsystem.component.indicator.JumpProgress
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.component.reward.JumpQuestCard
import com.example.jump.core.designsystem.component.reward.JumpQuestState
import com.example.jump.core.designsystem.component.reward.JumpStreakBadge
import com.example.jump.core.designsystem.component.reward.JumpXpProgress
import com.example.jump.core.designsystem.format.formatDuration
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.GamificationState
import com.example.jump.core.model.QuestId
import com.example.jump.core.model.QuestProgress
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutPlan

@Composable
fun HomeRoute(
  permissionError: CountingMode?,
  onStart: (WorkoutPlan, CountingMode) -> Unit,
  onContinue: () -> Unit,
  onConfigureWorkout: () -> Unit,
  viewModel: HomeViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  HomeScreen(
    state = state,
    quickPlan = viewModel.quickPlan,
    permissionError = permissionError,
    onStart = onStart,
    onContinue = onContinue,
    onConfigureWorkout = onConfigureWorkout,
    onCountingModeChange = viewModel::setCountingMode,
  )
}

@Composable
fun HomeScreen(
  state: HomeUiState,
  quickPlan: WorkoutPlan,
  permissionError: CountingMode?,
  onStart: (WorkoutPlan, CountingMode) -> Unit,
  onContinue: () -> Unit,
  onConfigureWorkout: () -> Unit,
  onCountingModeChange: (CountingMode) -> Unit,
) {
  when (state.loadState) {
    HomeLoadState.LOADING -> HomeLoading()
    HomeLoadState.ERROR -> HomeError()
    HomeLoadState.READY -> HomeContent(
      state = state,
      quickPlan = quickPlan,
      permissionError = permissionError,
      onStart = onStart,
      onContinue = onContinue,
      onConfigureWorkout = onConfigureWorkout,
      onCountingModeChange = onCountingModeChange,
    )
  }
}

@Composable
private fun HomeContent(
  state: HomeUiState,
  quickPlan: WorkoutPlan,
  permissionError: CountingMode?,
  onStart: (WorkoutPlan, CountingMode) -> Unit,
  onContinue: () -> Unit,
  onConfigureWorkout: () -> Unit,
  onCountingModeChange: (CountingMode) -> Unit,
) {
  JumpScreen {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item { TodayHeader(state.gamification.currentStreak) }
      item { MomentumOverview(state.gamification) }
      if (state.active.isRunning) {
        item {
          JumpCard(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
            Row(
              Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.workout_in_progress), style = MaterialTheme.typography.titleLarge)
                Text(
                  stringResource(
                    R.string.active_workout_summary,
                    state.active.detectedJumps,
                    formatDuration(state.active.elapsedMillis),
                  ),
                  color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.76f),
                )
              }
              JumpBadge(stringResource(R.string.live_badge))
            }
            JumpPrimaryButton(stringResource(R.string.continue_workout), onContinue, Modifier.fillMaxWidth())
          }
        }
      }
      item {
        DailyWorkoutHero(
          state = state,
          onCountingModeChange = onCountingModeChange,
          onStart = { state.dailyPlan?.let { onStart(it, state.countingMode) } },
        )
      }
      item {
        WeeklyGoalCard(
          completed = state.completedSessionsThisWeek,
          goal = state.profile.sessionsPerWeek,
        )
      }
      item { DailyQuest(state.gamification.primaryDailyQuest()) }
      item {
        MoreWorkouts(
          state = state,
          onConfigureWorkout = onConfigureWorkout,
          onStartQuickWorkout = { onStart(quickPlan, state.countingMode) },
        )
      }
      permissionError?.let { mode ->
        item {
          JumpInfoBanner(
            title = stringResource(
              if (mode == CountingMode.CAMERA) R.string.camera_access_needed else R.string.motion_access_needed,
            ),
            message = stringResource(
              if (mode == CountingMode.CAMERA) R.string.camera_access_message else R.string.motion_access_message,
            ),
            isError = true,
          )
        }
      }
      if (state.sessions.isEmpty()) {
        item {
          JumpEmptyState(
            title = stringResource(R.string.home_empty_title),
            description = stringResource(R.string.home_empty_description),
          )
        }
      }
    }
  }
}

@Composable
private fun TodayHeader(streak: Int) {
  Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      JumpBrandMark(Modifier.size(48.dp))
      Column {
        JumpEyebrow(stringResource(R.string.home_eyebrow))
        Text(
          stringResource(R.string.home_title),
          modifier = Modifier.semantics { heading() },
          style = MaterialTheme.typography.headlineMedium,
        )
      }
    }
    if (streak > 0) {
      JumpStreakBadge(streak.toString(), stringResource(R.string.day_streak_short))
    }
  }
}

@Composable
private fun MomentumOverview(gamification: GamificationState) {
  JumpCard {
    JumpXpProgress(
      label = stringResource(R.string.level_value, gamification.level),
      value = stringResource(
        R.string.xp_progress_value,
        gamification.xpInLevel,
        gamification.xpToNextLevel,
      ),
      progress = gamification.xpInLevel.toFloat() / gamification.xpToNextLevel.coerceAtLeast(1),
    )
    Text(
      stringResource(
        if (gamification.totalXp == 0) R.string.xp_first_workout_hint else R.string.xp_keep_moving_hint,
      ),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun DailyWorkoutHero(
  state: HomeUiState,
  onCountingModeChange: (CountingMode) -> Unit,
  onStart: () -> Unit,
) {
  val plan = state.dailyPlan
  JumpCard(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
  ) {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top,
    ) {
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          stringResource(R.string.adaptive_daily).uppercase(),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
          plan?.title ?: stringResource(R.string.preparing_workout),
          style = MaterialTheme.typography.headlineLarge,
        )
        plan?.let {
          Text(
            it.subtitle,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
          )
        }
      }
      Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        plan?.let { JumpBadge(stringResource(R.string.duration_minutes, (it.durationSeconds + 59) / 60)) }
        JumpRopeArtwork(Modifier.size(76.dp))
      }
    }
    CountingModeSelector(state.countingMode, onCountingModeChange)
    JumpPrimaryButton(
      label = stringResource(R.string.start_daily_workout),
      onClick = onStart,
      modifier = Modifier.fillMaxWidth(),
      enabled = plan != null && !state.active.isRunning,
    )
  }
}

@Composable
private fun JumpRopeArtwork(modifier: Modifier = Modifier) {
  val color = MaterialTheme.colorScheme.onPrimaryContainer
  Canvas(modifier) {
    val rope = Path().apply {
      moveTo(size.width * .18f, size.height * .18f)
      cubicTo(
        size.width * -.05f,
        size.height * .55f,
        size.width * .22f,
        size.height * .92f,
        size.width * .5f,
        size.height * .92f,
      )
      cubicTo(
        size.width * .78f,
        size.height * .92f,
        size.width * 1.05f,
        size.height * .55f,
        size.width * .82f,
        size.height * .18f,
      )
    }
    drawPath(rope, color.copy(alpha = .55f), style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
    drawLine(color, Offset(size.width * .12f, 0f), Offset(size.width * .22f, size.height * .28f), 7.dp.toPx(), StrokeCap.Round)
    drawLine(color, Offset(size.width * .88f, 0f), Offset(size.width * .78f, size.height * .28f), 7.dp.toPx(), StrokeCap.Round)
  }
}

@Composable
private fun CountingModeSelector(selected: CountingMode, onSelected: (CountingMode) -> Unit) {
  Text(
    stringResource(R.string.count_with),
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .78f),
  )
  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    CountingModeOption(
      label = stringResource(R.string.pocket_motion),
      selected = selected == CountingMode.MOTION,
      onClick = { onSelected(CountingMode.MOTION) },
      modifier = Modifier.weight(1f),
    )
    CountingModeOption(
      label = stringResource(R.string.camera_tracking),
      selected = selected == CountingMode.CAMERA,
      onClick = { onSelected(CountingMode.CAMERA) },
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun CountingModeOption(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = MaterialTheme.colorScheme
  Surface(
    selected = selected,
    onClick = onClick,
    modifier = modifier.heightIn(min = 48.dp),
    shape = MaterialTheme.shapes.small,
    color = if (selected) colors.primaryFixed else colors.surfaceContainerLow,
    contentColor = if (selected) colors.onPrimaryFixed else colors.onSurfaceVariant,
  ) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 14.dp), contentAlignment = Alignment.Center) {
      Text(label, style = MaterialTheme.typography.labelLarge)
    }
  }
}

@Composable
private fun WeeklyGoalCard(completed: Int, goal: Int) {
  JumpCard {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(stringResource(R.string.weekly_momentum), style = MaterialTheme.typography.titleLarge)
        Text(
          stringResource(R.string.weekly_session_count, completed, goal),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      JumpBadge(
        if (completed >= goal) stringResource(R.string.goal_met)
        else stringResource(R.string.sessions_to_go, (goal - completed).coerceAtLeast(0)),
      )
    }
    JumpProgress(completed.toFloat() / goal.coerceAtLeast(1))
  }
}

@Composable
private fun DailyQuest(quest: HomeQuestUi) {
  val isJumps = quest.id == QuestId.DAILY_JUMPS
  JumpQuestCard(
    title = stringResource(R.string.daily_quest_title),
    description = stringResource(
      if (isJumps) R.string.daily_quest_jumps_description else R.string.daily_quest_workout_description,
      quest.target,
    ),
    progressText = if (isJumps) {
      stringResource(R.string.daily_quest_jumps_progress, quest.progress, quest.target)
    } else {
      stringResource(R.string.daily_quest_workout_progress, quest.progress, quest.target)
    },
    statusText = if (quest.completed) {
      stringResource(R.string.quest_complete)
    } else {
      stringResource(R.string.quest_xp_reward, quest.id.xpReward)
    },
    progress = quest.progress.toFloat() / quest.target.coerceAtLeast(1),
    state = if (quest.completed) JumpQuestState.COMPLETED else JumpQuestState.IN_PROGRESS,
  )
}

@Composable
private fun MoreWorkouts(
  state: HomeUiState,
  onConfigureWorkout: () -> Unit,
  onStartQuickWorkout: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text(
      stringResource(R.string.more_workouts),
      modifier = Modifier.semantics { heading() },
      style = MaterialTheme.typography.titleLarge,
    )
    val config = state.intervalWorkoutConfig
    WorkoutRow(
      title = stringResource(R.string.custom_intervals),
      description = stringResource(
        R.string.custom_compact_summary,
        config.rounds,
        formatDuration(config.totalSeconds * 1_000L),
        state.intervalCalorieEstimate.asCalories(),
      ),
      action = stringResource(R.string.customize_action),
      enabled = !state.active.isRunning,
      onClick = onConfigureWorkout,
    )
    WorkoutRow(
      title = stringResource(R.string.free_jump),
      description = stringResource(R.string.free_jump_description),
      action = stringResource(R.string.start_action),
      enabled = !state.active.isRunning,
      onClick = onStartQuickWorkout,
    )
  }
}

@Composable
private fun WorkoutRow(
  title: String,
  description: String,
  action: String,
  enabled: Boolean,
  onClick: () -> Unit,
) {
  Surface(
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier.fillMaxWidth().heightIn(min = 76.dp).semantics { role = Role.Button },
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Row(
      Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(Modifier.size(42.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
        Box(contentAlignment = Alignment.Center) {
          Text(title.take(1), style = MaterialTheme.typography.titleLarge)
        }
      }
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
      Text(action, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
  }
}

@Composable
private fun HomeLoading() {
  JumpScreen {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CircularProgressIndicator()
        Text(stringResource(R.string.home_loading), color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
private fun HomeError() {
  JumpScreen {
    Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
      JumpInfoBanner(
        title = stringResource(R.string.home_error_title),
        message = stringResource(R.string.home_error_description),
        isError = true,
      )
    }
  }
}

@Composable
private fun WorkoutCalorieEstimate.asCalories(): String =
  stringResource(R.string.calorie_range, minimumCalories, maximumCalories)

@JumpLightDarkPreviews
@Composable
private fun HomeScreenPreview() {
  JumpTheme {
    HomeScreen(
      state = HomeUiState(
        loadState = HomeLoadState.READY,
        profile = UserProfile(onboardingComplete = true, sessionsPerWeek = 3),
        gamification = GamificationState(
          totalXp = 740,
          level = 2,
          xpInLevel = 240,
          xpToNextLevel = 500,
          currentStreak = 7,
          dailyQuests = listOf(QuestProgress(QuestId.DAILY_JUMPS, "today", 320, false)),
        ),
      ),
      quickPlan = WorkoutPlan("preview", "Free jump", "Open session", com.example.jump.core.model.WorkoutKind.QUICK),
      permissionError = null,
      onStart = { _, _ -> },
      onContinue = {},
      onConfigureWorkout = {},
      onCountingModeChange = {},
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun HomeLoadingPreview() {
  JumpTheme { HomeLoading() }
}
