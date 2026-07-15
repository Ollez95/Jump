package com.example.jump.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.jump.core.data.repository.WorkoutRepository
import com.example.jump.core.designsystem.component.JumpBadge
import com.example.jump.core.designsystem.component.JumpCard
import com.example.jump.core.designsystem.component.JumpDetailRow
import com.example.jump.core.designsystem.component.JumpEmptyState
import com.example.jump.core.designsystem.component.JumpHeader
import com.example.jump.core.designsystem.component.JumpScreen
import com.example.jump.core.designsystem.component.JumpSecondaryButton
import com.example.jump.core.designsystem.component.JumpStatCard
import com.example.jump.core.designsystem.component.formatDate
import com.example.jump.core.designsystem.component.formatDuration
import com.example.jump.core.model.WorkoutSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(workouts: WorkoutRepository) : ViewModel() {
  val sessions = workouts.sessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun HistoryRoute(onOpen: (Long) -> Unit, viewModel: HistoryViewModel = hiltViewModel()) {
  val sessions by viewModel.sessions.collectAsStateWithLifecycle()
  HistoryScreen(sessions, onOpen)
}

@Composable
fun HistoryScreen(sessions: List<WorkoutSession>, onOpen: (Long) -> Unit) {
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
      if (sessions.isEmpty()) item {
        JumpEmptyState("Your first session starts here", "Completed workouts will collect here with jumps, pace, time, and streak details.")
      }
      items(sessions, key = { it.id }) { session ->
        JumpCard(Modifier.clickable { onOpen(session.id) }) {
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
        }
      }
    }
  }
}

@HiltViewModel
class SessionDetailViewModel @Inject constructor(private val workouts: WorkoutRepository) : ViewModel() {
  val session = MutableStateFlow<WorkoutSession?>(null)
  fun load(id: Long) { viewModelScope.launch { session.value = workouts.session(id) } }
}

@Composable
fun SessionDetailRoute(sessionId: Long, onBack: () -> Unit, viewModel: SessionDetailViewModel = hiltViewModel()) {
  LaunchedEffect(sessionId) { viewModel.load(sessionId) }
  val session by viewModel.session.collectAsStateWithLifecycle()
  SessionDetailScreen(session, onBack)
}

@Composable
fun SessionDetailScreen(session: WorkoutSession?, onBack: () -> Unit) {
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item { JumpSecondaryButton("← Back", onBack) }
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
            JumpDetailRow("Average pace", "${session.metrics.averagePace} jpm")
            JumpDetailRow("Longest streak", "${session.metrics.longestStreak}")
            JumpDetailRow("Status", session.status.name.lowercase().replaceFirstChar { it.uppercase() })
          }
        }
      }
    }
  }
}
