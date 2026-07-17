package com.example.jump.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.button.JumpDestructiveButton
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpStatCard
import com.example.jump.core.designsystem.component.feedback.JumpEmptyState
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.layout.JumpDetailRow
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.component.navigation.JumpTopAppBar
import com.example.jump.core.designsystem.format.formatDate
import com.example.jump.core.designsystem.format.formatDuration
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.SessionStatus

@Composable
fun HistoryRoute(
  onOpen: (Long) -> Unit,
  onProgress: () -> Unit,
  viewModel: HistoryViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  when (val value = state) {
    HistoryUiState.Loading -> HistoryStatusScreen(
      title = stringResource(R.string.history_loading_title),
      message = stringResource(R.string.history_loading_message),
    )
    HistoryUiState.Error -> HistoryStatusScreen(
      title = stringResource(R.string.history_error_title),
      message = stringResource(R.string.history_error_message),
      isError = true,
    )
    is HistoryUiState.Loaded -> HistoryScreen(value.sessions, onOpen, onProgress)
  }
}

@Composable
private fun HistoryStatusScreen(title: String, message: String, isError: Boolean = false) {
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = stringResource(R.string.history_momentum),
          title = stringResource(R.string.history_title),
          description = stringResource(R.string.history_description),
        )
      }
      item { JumpInfoBanner(title, message, isError = isError) }
    }
  }
}

@Composable
fun HistoryScreen(
  sessions: List<HistorySessionUiModel>,
  onOpen: (Long) -> Unit,
  onProgress: () -> Unit,
) {
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = stringResource(R.string.history_momentum),
          title = stringResource(R.string.history_title),
          description = stringResource(R.string.history_description),
        )
      }
      item {
        val summary = historySummary(sessions)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          JumpStatCard(
            title = stringResource(R.string.history_total_jumps),
            value = summary.totalJumps.toString(),
            unit = stringResource(R.string.history_jumps_unit),
            modifier = Modifier.weight(1f),
            highlighted = true,
          )
          JumpStatCard(
            title = stringResource(R.string.history_workouts),
            value = summary.workouts.toString(),
            unit = stringResource(R.string.history_sessions_unit),
            modifier = Modifier.weight(1f),
          )
        }
      }
      item {
        JumpCard {
          ActivityMetric(
            label = stringResource(R.string.history_estimated_burn),
            value = stringResource(
              R.string.history_calorie_range,
              historySummary(sessions).minimumCalories,
              historySummary(sessions).maximumCalories,
            ),
          )
          Text(
            stringResource(R.string.history_calorie_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          JumpPrimaryButton(stringResource(R.string.history_insights), onProgress, Modifier.fillMaxWidth())
        }
      }
      if (sessions.isEmpty()) item {
        JumpEmptyState(
          stringResource(R.string.history_empty_title),
          stringResource(R.string.history_empty_message),
        )
      }
      if (sessions.isNotEmpty()) {
        item { JumpEyebrow(stringResource(R.string.history_recent_workouts)) }
      }
      items(sessions, key = { it.session.id }) { item ->
        val session = item.session
        JumpCard(
          modifier = Modifier
            .heightIn(min = 112.dp)
            .clickable { onOpen(session.id) }
            .semantics { role = Role.Button },
        ) {
          Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(session.title, style = MaterialTheme.typography.titleLarge)
                Text(formatDate(session.startedAtEpochMillis), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("${session.metrics.correctedJumps}", style = MaterialTheme.typography.headlineSmall)
                Text(stringResource(R.string.history_jumps_unit), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
              Text(formatDuration(session.durationMillis), style = MaterialTheme.typography.labelLarge)
              Text(
                stringResource(R.string.history_average_pace, session.metrics.averagePace),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ActivityMetric(label: String, value: String, modifier: Modifier = Modifier) {
  Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, style = MaterialTheme.typography.headlineSmall)
  }
}

internal data class HistorySummary(
  val workouts: Int,
  val totalJumps: Int,
  val minimumCalories: Int,
  val maximumCalories: Int,
)

internal fun historySummary(sessions: List<HistorySessionUiModel>): HistorySummary = HistorySummary(
  workouts = sessions.size,
  totalJumps = sessions.sumOf { it.session.metrics.correctedJumps },
  minimumCalories = sessions.sumOf { it.calorieEstimate.minimumCalories },
  maximumCalories = sessions.sumOf { it.calorieEstimate.maximumCalories },
)

@Composable
fun SessionDetailRoute(sessionId: Long, onBack: () -> Unit, viewModel: SessionDetailViewModel = hiltViewModel()) {
  LaunchedEffect(sessionId) { viewModel.load(sessionId) }
  val state by viewModel.state.collectAsStateWithLifecycle()
  SessionDetailScreen(state, onBack, onDelete = { viewModel.deleteSession(onBack) })
}

@Composable
fun SessionDetailScreen(
  state: SessionDetailUiState,
  onBack: () -> Unit,
  onDelete: () -> Unit,
) {
  val session = state.session
  var showDeleteConfirmation by remember { mutableStateOf(false) }
  JumpScreen(
    topBar = {
      JumpTopAppBar(
        title = stringResource(R.string.session_details_title),
        onBack = onBack,
        backContentDescription = stringResource(R.string.session_back),
      )
    },
  ) {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      if (state.status != SessionDetailStatus.LOADED || session == null) item {
        when (state.status) {
          SessionDetailStatus.LOADING -> JumpInfoBanner(
            stringResource(R.string.session_loading_title),
            stringResource(R.string.session_loading_message),
          )
          SessionDetailStatus.NOT_FOUND -> JumpEmptyState(
            stringResource(R.string.session_not_found_title),
            stringResource(R.string.session_not_found_message),
          )
          SessionDetailStatus.ERROR -> JumpInfoBanner(
            stringResource(R.string.session_error_title),
            stringResource(R.string.session_error_message),
            isError = true,
          )
          SessionDetailStatus.LOADED -> Unit
        }
      } else {
        item {
          JumpHeader(
            eyebrow = stringResource(R.string.session_recap),
            title = session.title,
            description = formatDate(session.startedAtEpochMillis),
          )
        }
        item {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            JumpStatCard(stringResource(R.string.session_total), "${session.metrics.correctedJumps}", stringResource(R.string.history_jumps_unit), Modifier.weight(1f), highlighted = true)
            JumpStatCard(stringResource(R.string.session_best_pace), "${session.metrics.bestPace}", stringResource(R.string.session_jpm), Modifier.weight(1f))
          }
        }
        item {
          JumpCard {
            Text(stringResource(R.string.session_workout_details), style = MaterialTheme.typography.titleLarge)
            JumpDetailRow(stringResource(R.string.session_detected), "${session.metrics.detectedJumps}")
            JumpDetailRow(stringResource(R.string.session_elapsed_time), formatDuration(session.durationMillis))
            JumpDetailRow(stringResource(R.string.session_active_time), formatDuration(session.activeMillis))
            JumpDetailRow(
              stringResource(R.string.session_estimated_calories, state.calorieEstimate.referenceWeightKg),
              calorieRange(state.calorieEstimate),
            )
            JumpDetailRow(stringResource(R.string.session_average_pace), stringResource(R.string.session_pace_value, session.metrics.averagePace))
            JumpDetailRow(stringResource(R.string.session_longest_streak), "${session.metrics.longestStreak}")
            JumpDetailRow(stringResource(R.string.session_status), sessionStatusLabel(session.status))
            if (session.intervals.isNotEmpty()) {
              val work = session.intervals.firstOrNull { it.type == IntervalType.WORK }
              val rest = session.intervals.firstOrNull { it.type == IntervalType.REST }
              JumpDetailRow(stringResource(R.string.session_rounds), "${session.intervals.count { it.type == IntervalType.WORK }}")
              JumpDetailRow(stringResource(R.string.session_jump_time), work?.let { stringResource(R.string.session_seconds, it.durationSeconds) }.orEmpty())
              JumpDetailRow(stringResource(R.string.session_rest_time), rest?.let { stringResource(R.string.session_seconds, it.durationSeconds) } ?: stringResource(R.string.session_off))
            }
            Text(
              stringResource(R.string.session_calorie_disclaimer),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        item {
          JumpDestructiveButton(
            label = stringResource(R.string.session_delete),
            onClick = { showDeleteConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
  if (showDeleteConfirmation && session != null) {
    DeleteSessionDialog(
      sessionTitle = session.title,
      onDismiss = { showDeleteConfirmation = false },
      onConfirm = {
        showDeleteConfirmation = false
        onDelete()
      },
    )
  }
}

@Composable
private fun DeleteSessionDialog(
  sessionTitle: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.session_delete_title)) },
    text = { Text(stringResource(R.string.session_delete_message, sessionTitle)) },
    confirmButton = {
      TextButton(
        onClick = onConfirm,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
      ) {
        Text(stringResource(R.string.session_delete_confirm))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text(stringResource(R.string.session_delete_keep)) }
    },
  )
}

@Composable
private fun calorieRange(estimate: WorkoutCalorieEstimate): String = stringResource(
  R.string.session_calorie_range,
  estimate.minimumCalories,
  estimate.maximumCalories,
)

@Composable
private fun sessionStatusLabel(status: SessionStatus): String = stringResource(
  when (status) {
    SessionStatus.COMPLETED -> R.string.session_status_completed
    SessionStatus.CANCELLED -> R.string.session_status_cancelled
    SessionStatus.INTERRUPTED -> R.string.session_status_interrupted
  },
)

@JumpLightDarkPreviews
@Preview(name = "1.5x font", widthDp = 390, heightDp = 884, fontScale = 1.5f, showBackground = true)
@Composable
private fun HistoryScreenPreview() {
  JumpTheme { HistoryScreen(sessions = emptyList(), onOpen = {}, onProgress = {}) }
}
