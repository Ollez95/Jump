package com.example.jump.feature.workoutsetup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PauseCircleOutline
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.component.navigation.JumpTopAppBar
import com.example.jump.core.designsystem.format.formatDuration
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.IntervalWorkoutConfig
import com.example.jump.core.model.SessionPhase
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
        title = stringResource(R.string.setup_title),
        onBack = onBack,
        backContentDescription = stringResource(R.string.setup_back),
      )
    },
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(
        start = JumpSpacing.screen,
        top = JumpSpacing.xl,
        end = JumpSpacing.screen,
        bottom = JumpSpacing.xxl,
      ),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.md),
    ) {
      item {
        WorkoutEstimateCard(config, state.calorieEstimate)
      }
      item {
        IntervalStepperCard(
          title = stringResource(R.string.setup_jump_duration),
          description = stringResource(R.string.setup_jump_description),
          value = stringResource(R.string.setup_seconds_value, config.jumpSeconds),
          icon = Icons.Rounded.Timer,
          onDecrease = { onConfigurationChange { it.copy(jumpSeconds = it.jumpSeconds - 5) } },
          onIncrease = { onConfigurationChange { it.copy(jumpSeconds = it.jumpSeconds + 5) } },
          decreaseEnabled = config.jumpSeconds > IntervalWorkoutConfig.MIN_JUMP_SECONDS,
          increaseEnabled = config.jumpSeconds < IntervalWorkoutConfig.MAX_JUMP_SECONDS,
        )
      }
      item {
        IntervalStepperCard(
          title = stringResource(R.string.setup_rest_duration),
          description = stringResource(R.string.setup_rest_description),
          value = if (config.restSeconds == 0) {
            stringResource(R.string.setup_rest_off)
          } else {
            stringResource(R.string.setup_seconds_value, config.restSeconds)
          },
          icon = Icons.Rounded.PauseCircleOutline,
          onDecrease = { onConfigurationChange { it.copy(restSeconds = it.restSeconds - 5) } },
          onIncrease = { onConfigurationChange { it.copy(restSeconds = it.restSeconds + 5) } },
          decreaseEnabled = config.restSeconds > IntervalWorkoutConfig.MIN_REST_SECONDS,
          increaseEnabled = config.restSeconds < IntervalWorkoutConfig.MAX_REST_SECONDS,
        )
      }
      item {
        IntervalStepperCard(
          title = stringResource(R.string.setup_rounds),
          description = stringResource(R.string.setup_rounds_description),
          value = stringResource(R.string.setup_rounds_value, config.rounds),
          icon = Icons.Rounded.Repeat,
          onDecrease = { onConfigurationChange { it.copy(rounds = it.rounds - 1) } },
          onIncrease = { onConfigurationChange { it.copy(rounds = it.rounds + 1) } },
          decreaseEnabled = config.rounds > IntervalWorkoutConfig.MIN_ROUNDS,
          increaseEnabled = config.rounds < IntervalWorkoutConfig.MAX_ROUNDS,
        )
      }
      item {
        Text(
          text = stringResource(R.string.setup_tracking_method).uppercase(),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = JumpSpacing.xs, top = JumpSpacing.xs),
        )
      }
      item {
        TrackingMethodSelector(
          selected = state.countingMode,
          onSelected = onCountingModeChange,
        )
      }
      if (state.activeWorkout.isRunning) {
        item {
          JumpInfoBanner(
            title = stringResource(R.string.setup_active_title),
            message = stringResource(R.string.setup_active_message),
            isError = true,
          )
        }
      }
      item {
        JumpPrimaryButton(
          label = stringResource(R.string.setup_start),
          onClick = onStart,
          modifier = Modifier.fillMaxWidth(),
          enabled = !state.activeWorkout.isRunning,
        )
      }
    }
  }
}

@Composable
private fun WorkoutEstimateCard(
  config: IntervalWorkoutConfig,
  calories: WorkoutCalorieEstimate,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.extraLarge,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    BoxWithConstraints(Modifier.fillMaxWidth().padding(JumpSpacing.lg)) {
      val compact = maxWidth < 300.dp
      if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(JumpSpacing.md)) {
          EstimateDuration(config, Modifier.fillMaxWidth())
          EstimateBurn(calories, Alignment.Start)
        }
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(JumpSpacing.lg),
        ) {
          EstimateDuration(config, Modifier.weight(1f))
          Surface(
            modifier = Modifier.size(width = 1.dp, height = 64.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
          ) {}
          EstimateBurn(calories, Alignment.End)
        }
      }
    }
  }
}

@Composable
private fun EstimateDuration(config: IntervalWorkoutConfig, modifier: Modifier = Modifier) {
  Column(modifier) {
    Text(
      text = stringResource(R.string.setup_estimated_workout).uppercase(),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = formatDuration(config.totalSeconds * 1_000L),
      style = MaterialTheme.typography.displaySmall,
      color = MaterialTheme.colorScheme.primary,
    )
  }
}

@Composable
private fun EstimateBurn(calories: WorkoutCalorieEstimate, alignment: Alignment.Horizontal) {
  Column(horizontalAlignment = alignment) {
    Text(
      text = stringResource(R.string.setup_estimated_burn).uppercase(),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(JumpSpacing.xs),
    ) {
      Icon(
        imageVector = Icons.Rounded.LocalFireDepartment,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.tertiaryFixedDim,
      )
      Text(
        text = stringResource(
          R.string.setup_calorie_range,
          calories.minimumCalories,
          calories.maximumCalories,
        ),
        style = MaterialTheme.typography.titleLarge,
      )
    }
  }
}

@Composable
private fun IntervalStepperCard(
  title: String,
  description: String,
  value: String,
  icon: ImageVector,
  onDecrease: () -> Unit,
  onIncrease: () -> Unit,
  decreaseEnabled: Boolean,
  increaseEnabled: Boolean,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.extraLarge,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier.padding(JumpSpacing.lg),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.md),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          contentColor = MaterialTheme.colorScheme.primary,
        ) {
          Icon(icon, contentDescription = null, modifier = Modifier.padding(JumpSpacing.sm).size(24.dp))
        }
        Column(Modifier.weight(1f)) {
          Text(title, style = MaterialTheme.typography.titleLarge)
          Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = JumpSpacing.sm, vertical = JumpSpacing.xs),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(JumpSpacing.xs),
        ) {
          StepButton(
            symbol = "−",
            description = stringResource(R.string.setup_decrease, title),
            onClick = onDecrease,
            enabled = decreaseEnabled,
          )
          Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
          )
          StepButton(
            symbol = "+",
            description = stringResource(R.string.setup_increase, title),
            onClick = onIncrease,
            enabled = increaseEnabled,
          )
        }
      }
    }
  }
}

@Composable
private fun StepButton(
  symbol: String,
  description: String,
  onClick: () -> Unit,
  enabled: Boolean,
) {
  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier.size(56.dp).semantics { contentDescription = description },
    shape = CircleShape,
    contentPadding = PaddingValues(0.dp),
    border = null,
  ) {
    Text(symbol, style = MaterialTheme.typography.headlineLarge)
  }
}

@Composable
private fun TrackingMethodSelector(
  selected: CountingMode,
  onSelected: (CountingMode) -> Unit,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Row(Modifier.padding(JumpSpacing.xs), horizontalArrangement = Arrangement.spacedBy(JumpSpacing.xs)) {
      TrackingMethod(
        label = stringResource(R.string.setup_pocket),
        description = stringResource(R.string.setup_pocket_description),
        icon = Icons.Rounded.PhoneAndroid,
        selected = selected == CountingMode.MOTION,
        onClick = { onSelected(CountingMode.MOTION) },
        modifier = Modifier.weight(1f),
      )
      TrackingMethod(
        label = stringResource(R.string.setup_camera),
        description = stringResource(R.string.setup_camera_description),
        icon = Icons.Rounded.Videocam,
        selected = selected == CountingMode.CAMERA,
        onClick = { onSelected(CountingMode.CAMERA) },
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun TrackingMethod(
  label: String,
  description: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier
      .semantics(mergeDescendants = true) {
        this.selected = selected
        role = Role.RadioButton
        contentDescription = "$label. $description"
      }
      .clickable(onClick = onClick),
    shape = MaterialTheme.shapes.medium,
    color = if (selected) {
      MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
      MaterialTheme.colorScheme.surfaceContainerLow
    },
    contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
    border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = JumpSpacing.sm, vertical = JumpSpacing.md),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      Icon(icon, contentDescription = null)
      Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = JumpSpacing.xs))
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun WorkoutSetupScreenPreview() {
  JumpTheme {
    WorkoutSetupScreen(
      state = WorkoutSetupUiState(),
      onConfigurationChange = {},
      onCountingModeChange = {},
      onStart = {},
      onBack = {},
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun WorkoutSetupRunningPreview() {
  JumpTheme {
    WorkoutSetupScreen(
      state = WorkoutSetupUiState(
        activeWorkout = ActiveWorkoutState(phase = SessionPhase.ACTIVE),
        countingMode = CountingMode.CAMERA,
      ),
      onConfigurationChange = {},
      onCountingModeChange = {},
      onStart = {},
      onBack = {},
    )
  }
}
