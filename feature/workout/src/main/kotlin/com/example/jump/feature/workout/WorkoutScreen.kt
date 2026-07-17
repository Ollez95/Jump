package com.example.jump.feature.workout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.camera.CameraJumpPreview
import com.example.jump.core.camera.CameraTrackingState
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.button.JumpSecondaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.indicator.JumpMetric
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.component.reward.JumpQuestCard
import com.example.jump.core.designsystem.component.reward.JumpQuestState
import com.example.jump.core.designsystem.component.reward.JumpRewardCard
import com.example.jump.core.designsystem.component.reward.JumpStreakBadge
import com.example.jump.core.designsystem.component.reward.JumpXpProgress
import com.example.jump.core.designsystem.format.formatDuration
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.domain.GamificationRules
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.QuestId
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutRewardResult

@Composable
fun WorkoutRoute(onDone: () -> Unit, viewModel: WorkoutViewModel = hiltViewModel()) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val reward by viewModel.completionReward.collectAsStateWithLifecycle()
  val calorieEstimate by viewModel.calorieEstimate.collectAsStateWithLifecycle()
  WorkoutScreen(
    state = state,
    rewardState = reward,
    calorieEstimate = calorieEstimate,
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
  rewardState: CompletionRewardUiState = CompletionRewardUiState.NotReady,
  calorieEstimate: WorkoutCalorieEstimate = WorkoutCalorieEstimate(),
  onCameraJump: () -> Unit = {},
  onCameraInactive: () -> Unit = {},
) {
  when {
    state.phase == SessionPhase.COMPLETED -> CompletionScreen(
      state = state,
      rewardState = rewardState,
      calorieEstimate = calorieEstimate,
      onCorrect = onCorrect,
      onDone = onDone,
    )
    state.countingMode == CountingMode.CAMERA -> CameraWorkoutScreen(
      state = state,
      onTogglePause = onTogglePause,
      onFinish = onFinish,
      onCameraJump = onCameraJump,
      onCameraInactive = onCameraInactive,
    )
    else -> ActiveWorkoutScreen(
      state = state,
      onTogglePause = onTogglePause,
      onFinish = onFinish,
    )
  }
}

@Composable
private fun ActiveWorkoutScreen(
  state: ActiveWorkoutState,
  onTogglePause: () -> Unit,
  onFinish: () -> Unit,
) {
  val round = state.roundInfo()
  val phase = state.phaseLabel()
  JumpScreen {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = JumpSpacing.screen, vertical = JumpSpacing.lg),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.lg),
    ) {
      Text(
        text = state.plan?.title ?: stringResource(R.string.workout_default_title),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.semantics { heading() },
      )
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        WorkoutLabelValue(
          label = stringResource(R.string.workout_phase_label),
          value = phase,
          alignment = Alignment.Start,
        )
        round?.let {
          WorkoutLabelValue(
            label = stringResource(R.string.workout_round_label),
            value = stringResource(R.string.workout_round_value, it.current, it.total),
            alignment = Alignment.End,
          )
        }
      }
      BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        CircularIntervalCounter(
          state = state,
          modifier = Modifier.size(maxWidth.coerceAtMost(286.dp)),
        )
      }
      if (!state.sensorAvailable) {
        JumpInfoBanner(
          title = stringResource(R.string.workout_manual_timing_title),
          message = stringResource(R.string.workout_manual_timing_message),
          isError = true,
        )
      }
      JumpCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
          JumpMetric(
            label = stringResource(R.string.workout_pace),
            value = state.currentPace.toString(),
            unit = stringResource(R.string.workout_pace_unit),
            modifier = Modifier.weight(1f),
          )
          JumpMetric(
            label = stringResource(R.string.workout_best_streak),
            value = state.longestStreak.toString(),
            unit = stringResource(R.string.workout_jumps_unit),
            modifier = Modifier.weight(1f),
          )
          JumpMetric(
            label = stringResource(R.string.workout_time),
            value = formatDuration(state.elapsedMillis),
            unit = stringResource(R.string.workout_elapsed),
            modifier = Modifier.weight(1f),
          )
        }
      }
      PauseButton(
        paused = state.phase == SessionPhase.PAUSED,
        enabled = state.phase != SessionPhase.PREPARING,
        onClick = onTogglePause,
      )
      JumpPrimaryButton(
        label = stringResource(R.string.workout_finish_save),
        onClick = onFinish,
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(Modifier.height(JumpSpacing.xs))
    }
  }
}

@Composable
private fun WorkoutLabelValue(label: String, value: String, alignment: Alignment.Horizontal) {
  Column(horizontalAlignment = alignment) {
    Text(
      text = label.uppercase(),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value.uppercase(),
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.primary,
    )
  }
}

@Composable
private fun CircularIntervalCounter(state: ActiveWorkoutState, modifier: Modifier = Modifier) {
  val progress = state.intervalProgress()
  val animatedProgress by animateFloatAsState(progress, label = "interval-progress")
  val progressDescription = stringResource(R.string.workout_progress_description, (progress * 100).toInt())
  val remaining = if (state.phase == SessionPhase.PREPARING || state.plan?.intervals?.isNotEmpty() == true) {
    formatDuration(state.intervalRemainingMillis.coerceAtMost(86_400_000L))
  } else {
    formatDuration(state.elapsedMillis)
  }
  val track = MaterialTheme.colorScheme.outlineVariant
  val indicator = MaterialTheme.colorScheme.primary
  val container = MaterialTheme.colorScheme.surfaceContainerLowest
  Box(
    modifier = modifier
      .semantics(mergeDescendants = true) {
        progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f)
        contentDescription = progressDescription
      },
    contentAlignment = Alignment.Center,
  ) {
    Canvas(Modifier.fillMaxSize()) {
      val strokeWidth = 10.dp.toPx()
      val inset = strokeWidth / 2
      val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
      drawArc(
        color = track,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(inset, inset),
        size = arcSize,
        style = Stroke(strokeWidth, cap = StrokeCap.Round),
      )
      drawArc(
        color = indicator,
        startAngle = -90f,
        sweepAngle = animatedProgress * 360f,
        useCenter = false,
        topLeft = Offset(inset, inset),
        size = arcSize,
        style = Stroke(strokeWidth, cap = StrokeCap.Round),
      )
    }
    Surface(
      modifier = Modifier.fillMaxSize().padding(21.dp),
      shape = CircleShape,
      color = container,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
      Column(
        modifier = Modifier.padding(JumpSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Text(
          text = remaining,
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = state.detectedJumps.toString(),
          style = MaterialTheme.typography.displayLarge,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = stringResource(R.string.workout_jumps).uppercase(),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun PauseButton(paused: Boolean, enabled: Boolean, onClick: () -> Unit) {
  val description = stringResource(if (paused) R.string.workout_resume else R.string.workout_pause)
  Surface(
    shape = CircleShape,
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    IconButton(
      onClick = onClick,
      enabled = enabled,
      modifier = Modifier.size(72.dp).semantics { contentDescription = description },
    ) {
      Icon(
        imageVector = if (paused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
        contentDescription = null,
        modifier = Modifier.size(32.dp),
      )
    }
  }
}

@Composable
private fun CameraWorkoutScreen(
  state: ActiveWorkoutState,
  onTogglePause: () -> Unit,
  onFinish: () -> Unit,
  onCameraJump: () -> Unit,
  onCameraInactive: () -> Unit,
) {
  var trackingState by remember { mutableStateOf(CameraTrackingState.CALIBRATING) }
  val view = LocalView.current
  DisposableEffect(view) {
    val previous = view.keepScreenOn
    view.keepScreenOn = true
    onDispose { view.keepScreenOn = previous }
  }
  LifecycleStartEffect(onCameraInactive) {
    onStopOrDispose { onCameraInactive() }
  }
  val status = when (trackingState) {
    CameraTrackingState.CALIBRATING -> stringResource(R.string.workout_camera_calibrating)
    CameraTrackingState.TRACKING -> stringResource(R.string.workout_camera_tracking)
    CameraTrackingState.PERSON_NOT_VISIBLE -> stringResource(R.string.workout_camera_missing)
    CameraTrackingState.CAMERA_ERROR -> stringResource(R.string.workout_camera_error)
  }
  val overlay = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.86f)
  val overlayContent = MaterialTheme.colorScheme.inverseOnSurface
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.inverseSurface),
  ) {
    CameraJumpPreview(
      onJump = onCameraJump,
      onTrackingState = { trackingState = it },
      modifier = Modifier.fillMaxSize(),
    )
    CameraFramingGuide(Modifier.fillMaxSize())
    Row(
      modifier = Modifier.fillMaxWidth().padding(JumpSpacing.lg),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(shape = CircleShape, color = overlay, contentColor = overlayContent) {
        Icon(
          imageVector = Icons.Rounded.Visibility,
          contentDescription = null,
          modifier = Modifier.padding(JumpSpacing.sm).size(28.dp),
        )
      }
      Surface(shape = CircleShape, color = overlay, contentColor = overlayContent) {
        Row(
          modifier = Modifier.padding(horizontal = JumpSpacing.md, vertical = JumpSpacing.sm),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(JumpSpacing.xs),
        ) {
          Surface(Modifier.size(10.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryFixedDim) {}
          Text(stringResource(R.string.workout_camera_live).uppercase(), style = MaterialTheme.typography.labelLarge)
        }
      }
    }
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = JumpSpacing.lg).align(Alignment.Center),
      horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
    ) {
      Surface(
        modifier = Modifier.weight(1.2f),
        shape = MaterialTheme.shapes.extraLarge,
        color = overlay,
        contentColor = overlayContent,
      ) {
        Column(Modifier.padding(JumpSpacing.lg)) {
          Text(stringResource(R.string.workout_camera_status).uppercase(), style = MaterialTheme.typography.labelLarge)
          Text(status, style = MaterialTheme.typography.headlineMedium)
        }
      }
      Surface(
        modifier = Modifier.weight(0.8f),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.92f),
        contentColor = MaterialTheme.colorScheme.onSurface,
      ) {
        Column(Modifier.padding(JumpSpacing.lg), horizontalAlignment = Alignment.CenterHorizontally) {
          Text(stringResource(R.string.workout_jumps).uppercase(), style = MaterialTheme.typography.labelLarge)
          Text(state.detectedJumps.toString(), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
          Text(formatDuration(state.elapsedMillis), style = MaterialTheme.typography.titleSmall)
        }
      }
    }
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(horizontal = JumpSpacing.md, vertical = JumpSpacing.lg),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.md),
    ) {
      Surface(shape = CircleShape, color = overlay, contentColor = overlayContent) {
        Row(
          modifier = Modifier.padding(horizontal = JumpSpacing.md, vertical = JumpSpacing.sm),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(JumpSpacing.xs),
        ) {
          Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
          Text(stringResource(R.string.workout_camera_privacy), style = MaterialTheme.typography.bodySmall)
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(JumpSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        JumpSecondaryButton(
          label = stringResource(R.string.workout_finish_save),
          onClick = onFinish,
          modifier = Modifier.weight(1f),
        )
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
          val pauseDescription = stringResource(
            if (state.phase == SessionPhase.PAUSED) R.string.workout_resume else R.string.workout_pause,
          )
          IconButton(
            onClick = onTogglePause,
            enabled = state.phase != SessionPhase.PREPARING,
            modifier = Modifier.size(72.dp).semantics { contentDescription = pauseDescription },
          ) {
            Icon(
              imageVector = if (state.phase == SessionPhase.PAUSED) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
              contentDescription = null,
              modifier = Modifier.size(32.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CameraFramingGuide(modifier: Modifier = Modifier) {
  val guide = MaterialTheme.colorScheme.primaryFixed.copy(alpha = 0.52f)
  val guideDescription = stringResource(R.string.workout_camera_framing_guide)
  Canvas(modifier.semantics { contentDescription = guideDescription }) {
    val centerX = size.width / 2
    val top = size.height * 0.28f
    val bottom = size.height * 0.72f
    val stroke = 3.dp.toPx()
    drawCircle(guide, radius = size.minDimension * 0.075f, center = Offset(centerX, top), style = Stroke(stroke))
    drawLine(guide, Offset(centerX, top + size.minDimension * 0.075f), Offset(centerX, bottom * 0.67f), stroke, StrokeCap.Round)
    drawLine(guide, Offset(centerX, size.height * 0.42f), Offset(size.width * 0.32f, size.height * 0.54f), stroke, StrokeCap.Round)
    drawLine(guide, Offset(centerX, size.height * 0.42f), Offset(size.width * 0.68f, size.height * 0.54f), stroke, StrokeCap.Round)
    drawLine(guide, Offset(centerX, bottom * 0.67f), Offset(size.width * 0.38f, bottom), stroke, StrokeCap.Round)
    drawLine(guide, Offset(centerX, bottom * 0.67f), Offset(size.width * 0.62f, bottom), stroke, StrokeCap.Round)
  }
}

@Composable
private fun CompletionScreen(
  state: ActiveWorkoutState,
  rewardState: CompletionRewardUiState,
  calorieEstimate: WorkoutCalorieEstimate,
  onCorrect: (Int) -> Unit,
  onDone: () -> Unit,
) {
  JumpScreen {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = JumpSpacing.screen, vertical = JumpSpacing.xxl),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.lg),
    ) {
      Icon(
        imageVector = Icons.Rounded.CheckCircle,
        contentDescription = null,
        modifier = Modifier.size(44.dp),
        tint = MaterialTheme.colorScheme.primary,
      )
      Text(
        text = stringResource(R.string.workout_complete_title),
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.semantics { heading() },
      )
      Text(
        text = stringResource(R.string.workout_complete_saved),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
      JumpCorrectionCard(state.correctedJumps, onCorrect)
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm)) {
        CompletionMetric(
          label = stringResource(R.string.workout_time),
          value = formatDuration(state.elapsedMillis),
          modifier = Modifier.weight(1f),
        )
        CompletionMetric(
          label = stringResource(R.string.workout_calories),
          value = calorieEstimate.displayRange(),
          modifier = Modifier.weight(1f),
        )
      }
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm)) {
        CompletionMetric(
          label = stringResource(R.string.workout_pace),
          value = state.bestPace.toString(),
          modifier = Modifier.weight(1f),
        )
        CompletionMetric(
          label = stringResource(R.string.workout_best_streak),
          value = state.longestStreak.toString(),
          modifier = Modifier.weight(1f),
        )
      }
      RewardSummary(rewardState)
      JumpPrimaryButton(
        label = stringResource(R.string.workout_done),
        onClick = onDone,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
private fun JumpCorrectionCard(jumps: Int, onCorrect: (Int) -> Unit) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.extraLarge,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier.padding(JumpSpacing.lg),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
    ) {
      Text(
        text = stringResource(R.string.workout_total_jumps).uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(JumpSpacing.lg)) {
        CorrectionButton(
          symbol = "−",
          description = stringResource(R.string.workout_decrease_jumps),
          onClick = { onCorrect(jumps - 1) },
        )
        Text(jumps.toString(), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        CorrectionButton(
          symbol = "+",
          description = stringResource(R.string.workout_increase_jumps),
          onClick = { onCorrect(jumps + 1) },
        )
      }
    }
  }
}

@Composable
private fun CorrectionButton(symbol: String, description: String, onClick: () -> Unit) {
  Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
    IconButton(
      onClick = onClick,
      modifier = Modifier.size(56.dp).semantics { contentDescription = description },
    ) {
      Text(symbol, style = MaterialTheme.typography.headlineMedium)
    }
  }
}

@Composable
private fun CompletionMetric(label: String, value: String, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(Modifier.padding(JumpSpacing.md), verticalArrangement = Arrangement.spacedBy(JumpSpacing.xs)) {
      Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(value, style = MaterialTheme.typography.headlineMedium)
    }
  }
}

@Composable
private fun RewardSummary(rewardState: CompletionRewardUiState) {
  when (rewardState) {
    CompletionRewardUiState.NotReady,
    CompletionRewardUiState.Loading -> JumpCard {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm)) {
        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 3.dp)
        Text(stringResource(R.string.workout_reward_loading), style = MaterialTheme.typography.bodyMedium)
      }
    }
    CompletionRewardUiState.Unavailable -> JumpInfoBanner(
      title = stringResource(R.string.workout_reward_title),
      message = stringResource(R.string.workout_reward_unavailable),
    )
    is CompletionRewardUiState.Ready -> RewardDetails(rewardState.reward)
  }
}

@Composable
private fun RewardDetails(reward: WorkoutRewardResult) {
  val xpInLevel = reward.totalXpAfter % GamificationRules.XP_PER_LEVEL
  Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(JumpSpacing.md)) {
    JumpRewardCard(
      title = stringResource(R.string.workout_reward_title),
      value = stringResource(R.string.workout_reward_value, reward.totalAwardedXp),
      supportingText = if (reward.levelAfter > reward.levelBefore) {
        stringResource(R.string.workout_reward_level_up, reward.levelAfter)
      } else {
        stringResource(R.string.workout_reward_level_progress, reward.levelAfter)
      },
    )
    JumpCard {
      JumpXpProgress(
        label = stringResource(R.string.workout_reward_level_progress, reward.levelAfter),
        value = stringResource(R.string.workout_reward_xp_progress, xpInLevel, GamificationRules.XP_PER_LEVEL),
        progress = xpInLevel.toFloat() / GamificationRules.XP_PER_LEVEL,
      )
      JumpStreakBadge(
        value = reward.currentStreak.toString(),
        label = stringResource(R.string.workout_reward_streak),
      )
    }
    reward.completedQuests.firstOrNull()?.let { quest ->
      JumpQuestCard(
        title = stringResource(R.string.workout_quest_complete),
        description = quest.label(),
        progressText = stringResource(R.string.workout_quest_reward),
        statusText = stringResource(R.string.workout_quest_completed_status),
        progress = 1f,
        state = JumpQuestState.COMPLETED,
      )
    }
  }
}

@Composable
private fun QuestId.label(): String = stringResource(
  when (this) {
    QuestId.DAILY_WORKOUT -> R.string.workout_quest_daily_workout
    QuestId.DAILY_JUMPS -> R.string.workout_quest_daily_jumps
    QuestId.WEEKLY_WORKOUTS -> R.string.workout_quest_weekly_workouts
    QuestId.WEEKLY_JUMPS -> R.string.workout_quest_weekly_jumps
  },
)

internal data class RoundInfo(val current: Int, val total: Int)

internal fun ActiveWorkoutState.roundInfo(): RoundInfo? {
  val intervals = plan?.intervals.orEmpty()
  if (intervals.isEmpty()) return null
  val total = intervals.count { it.type == IntervalType.WORK }
  val throughCurrent = intervals
    .take(intervalIndex.coerceIn(0, intervals.lastIndex) + 1)
    .count { it.type == IntervalType.WORK }
  return RoundInfo(throughCurrent.coerceAtLeast(1).coerceAtMost(total), total)
}

@Composable
private fun ActiveWorkoutState.phaseLabel(): String = stringResource(
  when (phase) {
    SessionPhase.PREPARING -> R.string.workout_phase_preparing
    SessionPhase.ACTIVE -> if (calibrationRemainingMillis > 0) {
      R.string.workout_phase_calibrating
    } else {
      R.string.workout_phase_jump
    }
    SessionPhase.RESTING -> R.string.workout_phase_rest
    SessionPhase.PAUSED -> R.string.workout_phase_paused
    else -> R.string.workout_phase_workout
  },
)

internal fun ActiveWorkoutState.intervalProgress(): Float {
  if (phase == SessionPhase.PREPARING) return (1f - intervalRemainingMillis / 3_000f).coerceIn(0f, 1f)
  val interval = plan?.intervals?.getOrNull(intervalIndex) ?: return 0f
  val durationMillis = interval.durationSeconds * 1_000f
  return (1f - intervalRemainingMillis / durationMillis).coerceIn(0f, 1f)
}

internal fun WorkoutCalorieEstimate.displayRange(): String = when {
  maximumCalories <= 0 -> "0"
  minimumCalories == maximumCalories -> minimumCalories.toString()
  else -> "$minimumCalories–$maximumCalories"
}

private val previewPlan = WorkoutPlan(
  id = "preview",
  title = "Jump",
  subtitle = "",
  kind = WorkoutKind.CUSTOM,
  intervals = listOf(
    com.example.jump.core.model.WorkoutInterval(IntervalType.WORK, 60),
    com.example.jump.core.model.WorkoutInterval(IntervalType.REST, 15),
    com.example.jump.core.model.WorkoutInterval(IntervalType.WORK, 60),
  ),
)

@JumpLightDarkPreviews
@Composable
private fun ActiveWorkoutPreview() {
  JumpTheme {
    WorkoutScreen(
      state = ActiveWorkoutState(
        plan = previewPlan,
        phase = SessionPhase.ACTIVE,
        intervalRemainingMillis = 45_000,
        detectedJumps = 142,
        currentPace = 120,
        longestStreak = 88,
        elapsedMillis = 260_000,
      ),
      onTogglePause = {},
      onFinish = {},
      onCorrect = {},
      onDone = {},
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun CompletionPreview() {
  JumpTheme {
    WorkoutScreen(
      state = ActiveWorkoutState(
        savedSessionId = 12,
        plan = previewPlan,
        phase = SessionPhase.COMPLETED,
        correctedJumps = 852,
        elapsedMillis = 765_000,
        bestPace = 118,
        longestStreak = 92,
      ),
      rewardState = CompletionRewardUiState.Ready(
        WorkoutRewardResult(
          workoutSessionId = 12,
          baseXp = 75,
          questXp = 25,
          achievementXp = 50,
          totalAwardedXp = 150,
          totalXpAfter = 2_650,
          levelBefore = 5,
          levelAfter = 6,
          currentStreak = 4,
          completedQuests = setOf(QuestId.DAILY_WORKOUT),
          unlockedAchievements = emptySet(),
          awardedAtEpochMillis = 0,
        ),
      ),
      calorieEstimate = WorkoutCalorieEstimate(112, 166),
      onTogglePause = {},
      onFinish = {},
      onCorrect = {},
      onDone = {},
    )
  }
}
