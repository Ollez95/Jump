package com.example.jump.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.JumpBadge
import com.example.jump.core.designsystem.component.JumpCard
import com.example.jump.core.designsystem.component.JumpDestructiveButton
import com.example.jump.core.designsystem.component.JumpDetailRow
import com.example.jump.core.designsystem.component.JumpEmptyState
import com.example.jump.core.designsystem.component.JumpHeader
import com.example.jump.core.designsystem.component.JumpPrimaryButton
import com.example.jump.core.designsystem.component.JumpScreen
import com.example.jump.core.designsystem.component.JumpStatCard
import com.example.jump.core.designsystem.component.JumpTopAppBar
import com.example.jump.core.designsystem.component.formatDate
import com.example.jump.core.designsystem.component.formatDuration
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.WorkoutSession

@Composable
fun HistoryRoute(
  onOpen: (Long) -> Unit,
  onProgress: () -> Unit,
  viewModel: HistoryViewModel = hiltViewModel(),
) {
  val sessions by viewModel.sessions.collectAsStateWithLifecycle()
  HistoryScreen(sessions, onOpen, onProgress, viewModel::deleteSession)
}

@Composable
fun HistoryScreen(
  sessions: List<HistorySessionUiModel>,
  onOpen: (Long) -> Unit,
  onProgress: () -> Unit,
  onDelete: (Long) -> Unit,
) {
  var pendingDeletion by remember { mutableStateOf<WorkoutSession?>(null) }
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = "Your progress",
          title = "Workout history",
          description = "Every session is proof of momentum.",
          brandMark = true,
        )
      }
      item {
        JumpPrimaryButton("View progress & calendar", onProgress, Modifier.fillMaxWidth())
      }
      if (sessions.isEmpty()) item {
        JumpEmptyState("Your first session starts here", "Completed workouts will collect here with jumps, pace, time, and streak details.")
      }
      items(sessions, key = { it.session.id }) { item ->
        val session = item.session
        JumpCard {
          Column(
            Modifier.fillMaxWidth().clickable { onOpen(session.id) },
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(session.title, style = MaterialTheme.typography.titleLarge)
                Text(formatDate(session.startedAtEpochMillis), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              JumpBadge("${session.metrics.correctedJumps} JUMPS")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
              Text(formatDuration(session.durationMillis), style = MaterialTheme.typography.labelLarge)
              Text("${session.metrics.averagePace} avg jpm", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            JumpDetailRow(
              "Estimated burn (${item.calorieEstimate.referenceWeightKg} kg)",
              item.calorieEstimate.asCalories(),
            )
          }
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(
              onClick = { pendingDeletion = session },
              colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
              Text("Delete session")
            }
          }
        }
      }
    }
  }
  pendingDeletion?.let { session ->
    DeleteSessionDialog(
      sessionTitle = session.title,
      onDismiss = { pendingDeletion = null },
      onConfirm = {
        pendingDeletion = null
        onDelete(session.id)
      },
    )
  }
}

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
        title = "Session details",
        onBack = onBack,
        backContentDescription = "Back to history",
      )
    },
  ) {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      if (session == null) item { JumpEmptyState("Loading workout…", "Your session details are on the way.") } else {
        item {
          JumpHeader(
            eyebrow = "Session recap",
            title = session.title,
            description = formatDate(session.startedAtEpochMillis),
          )
        }
        item {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            JumpStatCard("Total", "${session.metrics.correctedJumps}", "jumps", Modifier.weight(1f), highlighted = true)
            JumpStatCard("Best pace", "${session.metrics.bestPace}", "jpm", Modifier.weight(1f))
          }
        }
        item {
          JumpCard {
            Text("Workout details", style = MaterialTheme.typography.titleLarge)
            JumpDetailRow("Detected", "${session.metrics.detectedJumps}")
            JumpDetailRow("Elapsed time", formatDuration(session.durationMillis))
            JumpDetailRow("Active time", formatDuration(session.activeMillis))
            JumpDetailRow(
              "Estimated calories (${state.calorieEstimate.referenceWeightKg} kg)",
              state.calorieEstimate.asCalories(),
            )
            JumpDetailRow("Average pace", "${session.metrics.averagePace} jpm")
            JumpDetailRow("Longest streak", "${session.metrics.longestStreak}")
            JumpDetailRow("Status", session.status.name.lowercase().replaceFirstChar { it.uppercase() })
            if (session.intervals.isNotEmpty()) {
              val work = session.intervals.firstOrNull { it.type == IntervalType.WORK }
              val rest = session.intervals.firstOrNull { it.type == IntervalType.REST }
              JumpDetailRow("Rounds", "${session.intervals.count { it.type == IntervalType.WORK }}")
              JumpDetailRow("Jump time", work?.let { "${it.durationSeconds}s" }.orEmpty())
              JumpDetailRow("Rest time", rest?.let { "${it.durationSeconds}s" } ?: "Off")
            }
            Text(
              "Calories are estimated from active jump time at a slow-to-fast pace. Actual burn varies by body weight, pace, technique, and fitness.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        item {
          JumpDestructiveButton(
            label = "Delete session",
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
    title = { Text("Delete session?") },
    text = { Text("$sessionTitle and its workout data will be permanently removed.") },
    confirmButton = {
      TextButton(
        onClick = onConfirm,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
      ) {
        Text("Delete")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Keep session") }
    },
  )
}

private fun WorkoutCalorieEstimate.asCalories(): String = "~$minimumCalories–$maximumCalories kcal"
