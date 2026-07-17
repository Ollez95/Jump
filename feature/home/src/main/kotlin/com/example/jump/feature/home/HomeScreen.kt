package com.example.jump.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.button.JumpSecondaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.indicator.JumpBadge
import com.example.jump.core.designsystem.component.indicator.JumpProgress
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.format.formatDuration
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.CountingMode
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
  HomeScreen(state, viewModel.quickPlan, permissionError, onStart, onContinue, onConfigureWorkout, viewModel::setCountingMode)
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
  val weeklyCount = state.completedSessionsThisWeek
  val weeklyGoal = state.profile.sessionsPerWeek
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = stringResource(R.string.home_eyebrow),
          title = stringResource(R.string.home_title),
          description = stringResource(
            if (weeklyCount >= weeklyGoal) R.string.home_goal_complete else R.string.home_next_ready,
          ),
        )
      }
      if (state.active.isRunning) {
        item {
          JumpCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
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
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
                )
              }
              JumpBadge(stringResource(R.string.live_badge))
            }
            JumpPrimaryButton(stringResource(R.string.continue_workout), onContinue, Modifier.fillMaxWidth())
          }
        }
      }
      item {
        DailyWorkoutCard(
          state = state,
          onCountingModeChange = onCountingModeChange,
          onStart = { state.dailyPlan?.let { onStart(it, state.countingMode) } },
        )
      }
      item {
        MoreWorkoutsCard(
          state = state,
          onConfigureWorkout = onConfigureWorkout,
          onStartQuickWorkout = { onStart(quickPlan, state.countingMode) },
        )
      }
      if (permissionError != null) {
        item {
          JumpInfoBanner(
            title = stringResource(
              if (permissionError == CountingMode.CAMERA) R.string.camera_access_needed else R.string.motion_access_needed,
            ),
            message = stringResource(
              if (permissionError == CountingMode.CAMERA) R.string.camera_access_message else R.string.motion_access_message,
            ),
            isError = true,
          )
        }
      }
      item {
        WeeklyOverview(
          completed = weeklyCount,
          goal = weeklyGoal,
          personalBest = state.personalBestJumps,
          sessions = state.sessions.size,
        )
      }
    }
  }
}

@Composable
private fun DailyWorkoutCard(
  state: HomeUiState,
  onCountingModeChange: (CountingMode) -> Unit,
  onStart: () -> Unit,
) {
  val plan = state.dailyPlan
  JumpCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        stringResource(R.string.adaptive_daily).uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
      )
      plan?.durationSeconds?.let {
        JumpBadge(stringResource(R.string.duration_minutes, (it + 59) / 60))
      }
    }
    Text(
      plan?.title ?: stringResource(R.string.preparing_workout),
      style = MaterialTheme.typography.headlineSmall,
    )
    if (plan != null) {
      Text(
        plan.subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
      )
    }
    CountingModeSelector(
      selectedMode = state.countingMode,
      onModeSelected = onCountingModeChange,
      labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
    )
    JumpPrimaryButton(
      label = stringResource(R.string.start_daily_workout),
      onClick = onStart,
      modifier = Modifier.fillMaxWidth(),
      enabled = plan != null && !state.active.isRunning,
    )
  }
}

@Composable
private fun CountingModeSelector(
  selectedMode: CountingMode,
  onModeSelected: (CountingMode) -> Unit,
  labelColor: androidx.compose.ui.graphics.Color,
) {
  Text(
    stringResource(R.string.count_with),
    style = MaterialTheme.typography.labelMedium,
    color = labelColor,
  )
  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    CountingModeOption(
      label = stringResource(R.string.pocket_motion),
      selected = selectedMode == CountingMode.MOTION,
      onClick = { onModeSelected(CountingMode.MOTION) },
      modifier = Modifier.weight(1f),
    )
    CountingModeOption(
      label = stringResource(R.string.camera_tracking),
      selected = selectedMode == CountingMode.CAMERA,
      onClick = { onModeSelected(CountingMode.CAMERA) },
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
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    color = if (selected) colors.primary else colors.surfaceContainerLow,
    contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant,
  ) {
    Text(
      label,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
      style = MaterialTheme.typography.labelLarge,
    )
  }
}

@Composable
private fun MoreWorkoutsCard(
  state: HomeUiState,
  onConfigureWorkout: () -> Unit,
  onStartQuickWorkout: () -> Unit,
) {
  val config = state.intervalWorkoutConfig
  JumpCard {
    Text(stringResource(R.string.more_workouts), style = MaterialTheme.typography.titleLarge)
    Text(stringResource(R.string.custom_intervals), style = MaterialTheme.typography.titleMedium)
    Text(
      stringResource(
        R.string.custom_workout_summary,
        config.jumpSeconds,
        if (config.restSeconds == 0) stringResource(R.string.rest_off) else stringResource(R.string.rest_seconds, config.restSeconds),
        config.rounds,
      ),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      stringResource(
        R.string.custom_workout_meta,
        formatDuration(config.totalSeconds * 1_000L),
        state.intervalCalorieEstimate.asCalories(),
        state.intervalCalorieEstimate.referenceWeightKg,
      ),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    JumpSecondaryButton(
      label = stringResource(R.string.customize_intervals),
      onClick = onConfigureWorkout,
      modifier = Modifier.fillMaxWidth(),
      enabled = !state.active.isRunning,
    )
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Text(stringResource(R.string.free_jump), style = MaterialTheme.typography.titleMedium)
    Text(
      stringResource(R.string.free_jump_description),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    JumpSecondaryButton(
      label = stringResource(R.string.start_free_jump),
      onClick = onStartQuickWorkout,
      modifier = Modifier.fillMaxWidth(),
      enabled = !state.active.isRunning,
    )
  }
}

@Composable
private fun WeeklyOverview(
  completed: Int,
  goal: Int,
  personalBest: Int,
  sessions: Int,
) {
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
        if (completed >= goal) {
          stringResource(R.string.goal_met)
        } else {
          stringResource(R.string.sessions_to_go, (goal - completed).coerceAtLeast(0))
        },
      )
    }
    JumpProgress(completed.toFloat() / goal.coerceAtLeast(1))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Row(Modifier.fillMaxWidth()) {
      OverviewMetric(
        label = stringResource(R.string.personal_best),
        value = stringResource(R.string.jumps_value, personalBest),
        modifier = Modifier.weight(1f),
      )
      OverviewMetric(
        label = stringResource(R.string.sessions),
        value = stringResource(R.string.all_time_value, sessions),
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun OverviewMetric(label: String, value: String, modifier: Modifier = Modifier) {
  Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, style = MaterialTheme.typography.titleLarge)
  }
}

@Composable
private fun WorkoutCalorieEstimate.asCalories(): String =
  stringResource(R.string.calorie_range, minimumCalories, maximumCalories)
