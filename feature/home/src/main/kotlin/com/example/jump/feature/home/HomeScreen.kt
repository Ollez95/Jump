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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.button.JumpSecondaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpChoiceCard
import com.example.jump.core.designsystem.component.card.JumpHeroCard
import com.example.jump.core.designsystem.component.card.JumpStatCard
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.indicator.JumpBadge
import com.example.jump.core.designsystem.component.indicator.JumpProgress
import com.example.jump.core.designsystem.component.layout.JumpDetailRow
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
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
  val best = state.personalBestJumps
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
        val config = state.intervalWorkoutConfig
        JumpCard {
          JumpEyebrow("Custom intervals")
          Text("Build your own workout", style = MaterialTheme.typography.titleLarge)
          Text("Set your jump time, recovery, and rounds. Your choices are remembered for next time.", color = MaterialTheme.colorScheme.onSurfaceVariant)
          JumpDetailRow("Jump", "${config.jumpSeconds}s per round")
          JumpDetailRow("Rest", if (config.restSeconds == 0) "Off" else "${config.restSeconds}s between rounds")
          JumpDetailRow("Rounds", "${config.rounds}")
          JumpDetailRow("Total", formatDuration(config.totalSeconds * 1_000L))
          JumpDetailRow(
            "Estimated burn (${state.intervalCalorieEstimate.referenceWeightKg} kg)",
            state.intervalCalorieEstimate.asCalories(),
          )
          JumpPrimaryButton("Customize intervals", onConfigureWorkout, Modifier.fillMaxWidth(), enabled = !state.active.isRunning)
        }
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

private fun WorkoutCalorieEstimate.asCalories(): String = "~$minimumCalories–$maximumCalories kcal"
