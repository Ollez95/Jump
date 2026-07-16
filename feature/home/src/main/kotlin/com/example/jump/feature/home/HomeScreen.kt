package com.example.jump.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.jump.core.data.repository.UserPreferencesRepository
import com.example.jump.core.data.repository.WorkoutRepository
import com.example.jump.core.designsystem.component.JumpCard
import com.example.jump.core.designsystem.component.JumpBadge
import com.example.jump.core.designsystem.component.JumpChoiceCard
import com.example.jump.core.designsystem.component.JumpEyebrow
import com.example.jump.core.designsystem.component.JumpHeader
import com.example.jump.core.designsystem.component.JumpHeroCard
import com.example.jump.core.designsystem.component.JumpInfoBanner
import com.example.jump.core.designsystem.component.JumpPrimaryButton
import com.example.jump.core.designsystem.component.JumpProgress
import com.example.jump.core.designsystem.component.JumpScreen
import com.example.jump.core.designsystem.component.JumpSecondaryButton
import com.example.jump.core.designsystem.component.JumpStatCard
import com.example.jump.core.designsystem.component.formatDuration
import com.example.jump.core.domain.AdaptiveWorkoutPlanner
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.SessionStatus
import com.example.jump.core.model.UserProfile
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutSession
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
  val profile: UserProfile = UserProfile(),
  val sessions: List<WorkoutSession> = emptyList(),
  val dailyPlan: WorkoutPlan? = null,
  val active: ActiveWorkoutState = ActiveWorkoutState(),
  val countingMode: CountingMode = CountingMode.MOTION,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val preferences: UserPreferencesRepository,
  workouts: WorkoutRepository,
  coordinator: WorkoutCoordinator,
  private val planner: AdaptiveWorkoutPlanner,
) : ViewModel() {
  val uiState: StateFlow<HomeUiState> = combine(preferences.profile, workouts.sessions, coordinator.state, preferences.countingMode) { profile, sessions, active, countingMode ->
    HomeUiState(profile, sessions, planner.createDailyPlan(profile, sessions), active, countingMode)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
  val quickPlan: WorkoutPlan = planner.quickPlan()
  fun setCountingMode(mode: CountingMode) = viewModelScope.launch { preferences.setCountingMode(mode) }
}

@Composable
fun HomeRoute(
  permissionError: CountingMode?,
  onStart: (WorkoutPlan, CountingMode) -> Unit,
  onContinue: () -> Unit,
  viewModel: HomeViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  HomeScreen(state, viewModel.quickPlan, permissionError, onStart, onContinue, viewModel::setCountingMode)
}

@Composable
fun HomeScreen(
  state: HomeUiState,
  quickPlan: WorkoutPlan,
  permissionError: CountingMode?,
  onStart: (WorkoutPlan, CountingMode) -> Unit,
  onContinue: () -> Unit,
  onCountingModeChange: (CountingMode) -> Unit,
) {
  val weekStart = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1_000L
  val weeklyCount = state.sessions.count { it.startedAtEpochMillis >= weekStart && it.status == SessionStatus.COMPLETED }
  val weeklyGoal = state.profile.sessionsPerWeek
  val best = state.sessions.maxOfOrNull { it.metrics.correctedJumps } ?: 0
  val dailyPlan = state.dailyPlan
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = "Today",
          title = "Ready to find\nyour rhythm?",
          description = if (weeklyCount >= weeklyGoal) "Weekly goal complete. Keep the momentum yours." else "Small sessions add up. Your next one is ready.",
          brandMark = true,
        )
      }
      if (state.active.isRunning) item {
        JumpCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Workout in progress", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
              Text("${state.active.detectedJumps} jumps • ${formatDuration(state.active.elapsedMillis)}", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
            }
            JumpBadge("LIVE")
          }
          JumpPrimaryButton("Continue workout", onContinue, Modifier.fillMaxWidth())
        }
      }
      item {
        JumpCard {
          JumpEyebrow("Counting method")
          Text("How should Jump count?", style = MaterialTheme.typography.titleLarge)
          JumpChoiceCard(
            label = "Pocket motion",
            description = "Keep your phone secure and count with motion sensors.",
            selected = state.countingMode == CountingMode.MOTION,
            onClick = { onCountingModeChange(CountingMode.MOTION) },
          )
          JumpChoiceCard(
            label = "Camera tracking",
            description = "Stand in frame and count jumps from your body movement.",
            selected = state.countingMode == CountingMode.CAMERA,
            onClick = { onCountingModeChange(CountingMode.CAMERA) },
          )
        }
      }
      item {
        JumpHeroCard(
          eyebrow = "Adaptive daily",
          title = dailyPlan?.title ?: "Preparing your workout",
          description = dailyPlan?.subtitle.orEmpty(),
          meta = dailyPlan?.durationSeconds?.let { "${(it + 59) / 60} min" },
          actionLabel = "Start daily workout",
          onAction = { dailyPlan?.let { onStart(it, state.countingMode) } },
          enabled = dailyPlan != null && !state.active.isRunning,
        )
      }
      item {
        JumpSecondaryButton(
          label = "Quick jump · open session",
          onClick = { onStart(quickPlan, state.countingMode) },
          modifier = Modifier.fillMaxWidth(),
          enabled = !state.active.isRunning,
        )
      }
      if (permissionError != null) item {
        JumpInfoBanner(
          title = if (permissionError == CountingMode.CAMERA) "Camera access needed" else "Motion access needed",
          message = if (permissionError == CountingMode.CAMERA) "Allow camera access so Jump can track your body without recording or saving video." else "Enable Physical activity for Jump in Android settings so your jumps can be counted.",
          isError = true,
        )
      }
      item {
        JumpCard {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text("Weekly momentum", style = MaterialTheme.typography.titleLarge)
              Text("$weeklyCount of $weeklyGoal sessions", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            JumpBadge(if (weeklyCount >= weeklyGoal) "GOAL MET" else "${(weeklyGoal - weeklyCount).coerceAtLeast(0)} TO GO")
          }
          JumpProgress(weeklyCount.toFloat() / weeklyGoal.coerceAtLeast(1))
        }
      }
      item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          JumpStatCard("Personal best", "$best", "jumps", Modifier.weight(1f), highlighted = best > 0)
          JumpStatCard("Sessions", "${state.sessions.size}", "all time", Modifier.weight(1f))
        }
      }
    }
  }
}
