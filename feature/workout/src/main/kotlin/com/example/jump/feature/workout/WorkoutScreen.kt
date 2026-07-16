package com.example.jump.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.viewModelScope
import com.example.jump.core.camera.CameraJumpPreview
import com.example.jump.core.camera.CameraTrackingState
import com.example.jump.core.designsystem.component.JumpBadge
import com.example.jump.core.designsystem.component.JumpBrandMark
import com.example.jump.core.designsystem.component.JumpCard
import com.example.jump.core.designsystem.component.JumpCounterOrb
import com.example.jump.core.designsystem.component.JumpInfoBanner
import com.example.jump.core.designsystem.component.JumpMetric
import com.example.jump.core.designsystem.component.JumpPrimaryButton
import com.example.jump.core.designsystem.component.JumpScreen
import com.example.jump.core.designsystem.component.JumpSecondaryButton
import com.example.jump.core.designsystem.component.formatDuration
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.workout.WorkoutController
import com.example.jump.core.workout.WorkoutCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutViewModel @Inject constructor(
  coordinator: WorkoutCoordinator,
  private val controller: WorkoutController,
) : ViewModel() {
  val state = coordinator.state
  private val workoutCoordinator = coordinator
  fun togglePause() = controller.togglePause()
  fun pauseCamera() = controller.pause()
  fun finish() = controller.stop()
  fun correct(jumps: Int) = viewModelScope.launch { workoutCoordinator.correctJumps(jumps) }
  fun registerCameraJump() = workoutCoordinator.registerJump()
  fun clear() = workoutCoordinator.clear()
}

@Composable
fun WorkoutRoute(onDone: () -> Unit, viewModel: WorkoutViewModel = hiltViewModel()) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  WorkoutScreen(
    state = state,
    onTogglePause = viewModel::togglePause,
    onFinish = viewModel::finish,
    onCorrect = viewModel::correct,
    onDone = { viewModel.clear(); onDone() },
    onCameraJump = viewModel::registerCameraJump,
    onCameraInactive = viewModel::pauseCamera,
  )
}

@Composable
fun WorkoutScreen(
  state: ActiveWorkoutState,
  onTogglePause: () -> Unit,
  onFinish: () -> Unit,
  onCorrect: (Int) -> Unit,
  onDone: () -> Unit,
  onCameraJump: () -> Unit = {},
  onCameraInactive: () -> Unit = {},
) {
  if (state.phase == SessionPhase.COMPLETED) { CompletionScreen(state, onCorrect, onDone); return }
  val phaseLabel = when (state.phase) {
    SessionPhase.PREPARING -> "GET READY"
    SessionPhase.ACTIVE -> if (state.calibrationRemainingMillis > 0) "CALIBRATING • KEEP JUMPING" else "JUMP"
    SessionPhase.RESTING -> "REST"
    SessionPhase.PAUSED -> "PAUSED"
    else -> "WORKOUT"
  }
  val intervalLabel = state.plan?.intervals?.takeIf { it.isNotEmpty() }?.let { intervals ->
    val totalRounds = intervals.count { it.type == IntervalType.WORK }
    val currentRound = intervals
      .take(state.intervalIndex.coerceIn(0, intervals.lastIndex) + 1)
      .count { it.type == IntervalType.WORK }
      .coerceAtLeast(1)
    val intervalName = if (state.phase == SessionPhase.RESTING) "Rest" else "Jump"
    "$intervalName • Round $currentRound of $totalRounds"
  }
  JumpScreen {
    Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
      Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          JumpBrandMark(Modifier.size(44.dp))
          Column(Modifier.weight(1f)) {
            Text(state.plan?.title.orEmpty(), style = MaterialTheme.typography.titleLarge)
            Text("LIVE WORKOUT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          JumpBadge(phaseLabel)
        }
        if (!state.sensorAvailable) {
          JumpInfoBanner("Manual timing", "No accelerometer was found, so timing will continue without automatic counting.", isError = true)
        }
      }
      Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (state.countingMode == CountingMode.CAMERA) {
          CameraCounterPanel(state.detectedJumps, onCameraJump, onCameraInactive)
        } else {
          JumpCounterOrb("${state.detectedJumps}", "Jumps")
        }
        Text(
          if (state.phase == SessionPhase.PREPARING) "${(state.intervalRemainingMillis + 999) / 1_000}" else formatDuration(state.intervalRemainingMillis.takeIf { it < 86_400_000 } ?: state.elapsedMillis),
          style = MaterialTheme.typography.headlineMedium,
        )
        Text(
          if (state.phase == SessionPhase.PREPARING) "Starting in seconds" else intervalLabel ?: "Open session",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      JumpCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
          JumpMetric("Pace", "${state.currentPace}", "jpm", Modifier.weight(1f))
          JumpMetric("Streak", "${state.longestStreak}", "best", Modifier.weight(1f))
          JumpMetric("Time", formatDuration(state.elapsedMillis), "elapsed", Modifier.weight(1f))
        }
      }
      Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        JumpPrimaryButton(if (state.phase == SessionPhase.PAUSED) "Resume" else "Pause", onTogglePause, Modifier.fillMaxWidth(), enabled = state.phase != SessionPhase.PREPARING)
        JumpSecondaryButton("Finish and save", onFinish, Modifier.fillMaxWidth())
      }
    }
  }
}

@Composable
private fun CameraCounterPanel(jumps: Int, onJump: () -> Unit, onCameraInactive: () -> Unit) {
  var trackingState by remember { mutableStateOf(CameraTrackingState.CALIBRATING) }
  val view = LocalView.current
  DisposableEffect(view) {
    val previous = view.keepScreenOn
    view.keepScreenOn = true
    onDispose {
      view.keepScreenOn = previous
    }
  }
  LifecycleStartEffect(onCameraInactive) {
    onStopOrDispose { onCameraInactive() }
  }
  val status = when (trackingState) {
    CameraTrackingState.CALIBRATING -> "CALIBRATING · STAND STILL"
    CameraTrackingState.TRACKING -> "BODY TRACKED"
    CameraTrackingState.PERSON_NOT_VISIBLE -> "STEP INTO FRAME"
    CameraTrackingState.CAMERA_ERROR -> "CAMERA UNAVAILABLE"
  }
  Box(
    Modifier
      .fillMaxWidth()
      .height(286.dp)
      .clip(RoundedCornerShape(28.dp))
      .background(Color.Black),
  ) {
    CameraJumpPreview(
      onJump = onJump,
      onTrackingState = { trackingState = it },
      modifier = Modifier.fillMaxSize(),
    )
    Box(
      Modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f)))),
    )
    Column(
      Modifier.align(Alignment.BottomStart).padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(status, style = MaterialTheme.typography.labelMedium, color = Color(0xFF65E6A7))
      Text("$jumps", style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.Black)
      Text("JUMPS · KEEP YOUR FULL BODY IN FRAME", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.78f))
    }
  }
}

@Composable
private fun CompletionScreen(state: ActiveWorkoutState, onCorrect: (Int) -> Unit, onDone: () -> Unit) {
  JumpScreen {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
      JumpBadge("MOMENTUM BUILT")
      Text("Nice work.", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 14.dp))
      Text("Another session is in the bank.", color = MaterialTheme.colorScheme.onSurfaceVariant)
      JumpCounterOrb("${state.correctedJumps}", "Total jumps", Modifier.padding(top = 28.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 18.dp)) {
        JumpSecondaryButton("− 1", { onCorrect(state.correctedJumps - 1) }, Modifier.weight(1f))
        JumpSecondaryButton("+ 1", { onCorrect(state.correctedJumps + 1) }, Modifier.weight(1f))
      }
      JumpCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
          JumpMetric("Detected", "${state.detectedJumps}", "jumps", Modifier.weight(1f))
          JumpMetric("Time", formatDuration(state.elapsedMillis), "elapsed", Modifier.weight(1f))
          JumpMetric("Pace", "${state.bestPace}", "best jpm", Modifier.weight(1f))
        }
      }
      JumpPrimaryButton("Done", onDone, Modifier.fillMaxWidth().padding(top = 22.dp))
    }
  }
}
