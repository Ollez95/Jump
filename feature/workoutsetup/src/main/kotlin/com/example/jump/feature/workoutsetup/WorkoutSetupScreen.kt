package com.example.jump.feature.workoutsetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.jump.core.designsystem.component.JumpCard
import com.example.jump.core.designsystem.component.JumpChoiceCard
import com.example.jump.core.designsystem.component.JumpDetailRow
import com.example.jump.core.designsystem.component.JumpEyebrow
import com.example.jump.core.designsystem.component.JumpHeader
import com.example.jump.core.designsystem.component.JumpInfoBanner
import com.example.jump.core.designsystem.component.JumpPrimaryButton
import com.example.jump.core.designsystem.component.JumpScreen
import com.example.jump.core.designsystem.component.JumpValueStepper
import com.example.jump.core.designsystem.component.JumpTopAppBar
import com.example.jump.core.designsystem.component.formatDuration
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.WorkoutPlan

@Composable
fun WorkoutSetupRoute(
  onBack: () -> Unit,
  onStart: (WorkoutPlan, CountingMode) -> Unit,
  viewModel: WorkoutSetupViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  WorkoutSetupScreen(
    state = state,
    onConfigurationChange = viewModel::updateConfiguration,
    onCountingModeChange = viewModel::updateCountingMode,
    onStart = { onStart(viewModel.createPlan(), state.countingMode) },
    onBack = onBack,
  )
}

@Composable
fun WorkoutSetupScreen(
  state: WorkoutSetupUiState,
  onConfigurationChange: ((IntervalWorkoutConfig) -> IntervalWorkoutConfig) -> Unit,
  onCountingModeChange: (CountingMode) -> Unit,
  onStart: () -> Unit,
  onBack: () -> Unit,
) {
  val config = state.configuration
  JumpScreen(
    topBar = {
      JumpTopAppBar(
        title = "Custom workout",
        onBack = onBack,
        backContentDescription = "Back to today",
      )
    },
  ) {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = "Interval builder",
          title = "Make every round yours.",
          description = "Choose how long you jump, recover, and how many rounds you want to complete.",
          brandMark = true,
        )
      }
      item {
        JumpCard {
          JumpEyebrow("Workout structure")
          JumpValueStepper(
            title = "Jump time",
            value = config.jumpSeconds.asDuration(),
            description = "Active time in each round",
            onDecrease = { onConfigurationChange { it.copy(jumpSeconds = it.jumpSeconds - 5) } },
            onIncrease = { onConfigurationChange { it.copy(jumpSeconds = it.jumpSeconds + 5) } },
            decreaseEnabled = config.jumpSeconds > IntervalWorkoutConfig.MIN_JUMP_SECONDS,
            increaseEnabled = config.jumpSeconds < IntervalWorkoutConfig.MAX_JUMP_SECONDS,
          )
          JumpValueStepper(
            title = "Rest time",
            value = if (config.restSeconds == 0) "Off" else config.restSeconds.asDuration(),
            description = "Recovery between rounds",
            onDecrease = { onConfigurationChange { it.copy(restSeconds = it.restSeconds - 5) } },
            onIncrease = { onConfigurationChange { it.copy(restSeconds = it.restSeconds + 5) } },
            decreaseEnabled = config.restSeconds > IntervalWorkoutConfig.MIN_REST_SECONDS,
            increaseEnabled = config.restSeconds < IntervalWorkoutConfig.MAX_REST_SECONDS,
          )
          JumpValueStepper(
            title = "Rounds",
            value = "${config.rounds}",
            description = "Jump sets in this workout",
            onDecrease = { onConfigurationChange { it.copy(rounds = it.rounds - 1) } },
            onIncrease = { onConfigurationChange { it.copy(rounds = it.rounds + 1) } },
            decreaseEnabled = config.rounds > IntervalWorkoutConfig.MIN_ROUNDS,
            increaseEnabled = config.rounds < IntervalWorkoutConfig.MAX_ROUNDS,
          )
        }
      }
      item {
        JumpCard {
          JumpEyebrow("Your workout")
          Text("${config.rounds} ${if (config.rounds == 1) "round" else "rounds"}", style = MaterialTheme.typography.headlineMedium)
          JumpDetailRow("Active jumping", formatDuration(config.activeSeconds * 1_000L))
          JumpDetailRow("Recovery", formatDuration(config.restSeconds * (config.rounds - 1).coerceAtLeast(0) * 1_000L))
          JumpDetailRow("Total time", formatDuration(config.totalSeconds * 1_000L))
          JumpDetailRow("Estimated burn", state.calorieEstimate.asCalories())
          Text(
            "Estimate for a ${state.calorieEstimate.referenceWeightKg} kg person at a slow-to-fast pace. Actual calories vary by body weight, pace, technique, and fitness.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      item {
        JumpCard {
          JumpEyebrow("Counting method")
          JumpChoiceCard(
            label = "Pocket motion",
            description = "Count from motion sensors while the phone is secured.",
            selected = state.countingMode == CountingMode.MOTION,
            onClick = { onCountingModeChange(CountingMode.MOTION) },
          )
          JumpChoiceCard(
            label = "Camera tracking",
            description = "Count from your movement while you remain in frame.",
            selected = state.countingMode == CountingMode.CAMERA,
            onClick = { onCountingModeChange(CountingMode.CAMERA) },
          )
        }
      }
      if (state.activeWorkout.isRunning) item {
        JumpInfoBanner("Workout already running", "Finish or resume your active workout before starting another one.", isError = true)
      }
      item {
        JumpPrimaryButton("Start custom workout", onStart, Modifier.fillMaxWidth(), enabled = !state.activeWorkout.isRunning)
      }
    }
  }
}

private fun Int.asDuration(): String = if (this < 60) "${this}s" else formatDuration(this * 1_000L)

private fun WorkoutCalorieEstimate.asCalories(): String = "~$minimumCalories–$maximumCalories kcal"
