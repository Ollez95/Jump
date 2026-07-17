package com.example.jump.core.designsystem.component.reward

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.component.indicator.JumpProgress
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

enum class JumpQuestState {
  IN_PROGRESS,
  COMPLETED,
  CLAIMABLE,
}

@Composable
fun JumpXpProgress(
  label: String,
  value: String,
  progress: Float,
  modifier: Modifier = Modifier,
) {
  val colors = MaterialTheme.colorScheme
  val normalizedProgress = progress.coerceIn(0f, 1f)
  Column(
    modifier = modifier.semantics {
      progressBarRangeInfo = ProgressBarRangeInfo(normalizedProgress, 0f..1f)
    },
    verticalArrangement = Arrangement.spacedBy(JumpSpacing.xs),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(JumpSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Rounded.Bolt,
          contentDescription = null,
          tint = colors.tertiaryFixedDim,
          modifier = Modifier.size(20.dp),
        )
        Text(label, style = MaterialTheme.typography.titleSmall)
      }
      Text(
        value,
        style = MaterialTheme.typography.labelLarge,
        color = colors.onSurfaceVariant,
      )
    }
    JumpProgress(
      progress = normalizedProgress,
      color = colors.tertiaryFixedDim,
      trackColor = colors.surfaceContainerHighest,
    )
  }
}

@Composable
fun JumpRewardCard(
  title: String,
  value: String,
  supportingText: String,
  modifier: Modifier = Modifier,
) {
  val colors = MaterialTheme.colorScheme
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.extraLarge,
    color = colors.tertiaryFixedDim,
    contentColor = colors.onTertiaryFixed,
  ) {
    Row(
      modifier = Modifier.padding(JumpSpacing.lg),
      horizontalArrangement = Arrangement.spacedBy(JumpSpacing.md),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = colors.onTertiaryFixed.copy(alpha = 0.1f),
        contentColor = colors.onTertiaryFixed,
      ) {
        Icon(
          imageVector = Icons.Rounded.Stars,
          contentDescription = null,
          modifier = Modifier.padding(JumpSpacing.sm).size(28.dp),
        )
      }
      Column(verticalArrangement = Arrangement.spacedBy(JumpSpacing.xxs)) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.headlineLarge)
        Text(
          supportingText,
          style = MaterialTheme.typography.bodyMedium,
          color = colors.onTertiaryFixedVariant,
        )
      }
    }
  }
}

@Composable
fun JumpQuestCard(
  title: String,
  description: String,
  progressText: String,
  statusText: String,
  progress: Float,
  state: JumpQuestState,
  modifier: Modifier = Modifier,
) {
  val colors = MaterialTheme.colorScheme
  val visual = when (state) {
    JumpQuestState.IN_PROGRESS -> QuestVisual(
      container = colors.secondaryContainer,
      content = colors.onSecondaryContainer,
      progress = colors.secondary,
      icon = Icons.Rounded.RadioButtonUnchecked,
    )
    JumpQuestState.COMPLETED -> QuestVisual(
      container = colors.primaryContainer,
      content = colors.onPrimaryContainer,
      progress = colors.primary,
      icon = Icons.Rounded.CheckCircle,
    )
    JumpQuestState.CLAIMABLE -> QuestVisual(
      container = colors.tertiaryFixedDim,
      content = colors.onTertiaryFixed,
      progress = colors.onTertiaryFixedVariant,
      icon = Icons.Rounded.Stars,
    )
  }
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    color = visual.container,
    contentColor = visual.content,
    border = BorderStroke(1.dp, visual.content.copy(alpha = 0.18f)),
  ) {
    Column(
      modifier = Modifier.padding(JumpSpacing.md),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
    ) {
      Row(verticalAlignment = Alignment.Top) {
        Icon(
          imageVector = visual.icon,
          contentDescription = null,
          modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(JumpSpacing.xs))
        Column(modifier = Modifier.weight(1f)) {
          Text(title, style = MaterialTheme.typography.titleMedium)
          Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = visual.content.copy(alpha = 0.78f),
          )
        }
      }
      Text(
        statusText,
        modifier = Modifier.align(Alignment.End),
        style = MaterialTheme.typography.labelMedium,
      )
      JumpProgress(
        progress = progress,
        color = visual.progress,
        trackColor = visual.content.copy(alpha = 0.14f),
      )
      Text(
        progressText,
        style = MaterialTheme.typography.labelMedium,
        color = visual.content.copy(alpha = 0.78f),
      )
    }
  }
}

@Composable
fun JumpStreakBadge(
  value: String,
  label: String,
  modifier: Modifier = Modifier,
) {
  val colors = MaterialTheme.colorScheme
  Surface(
    modifier = modifier,
    shape = CircleShape,
    color = colors.tertiaryFixedDim,
    contentColor = colors.onTertiaryFixed,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = JumpSpacing.sm, vertical = JumpSpacing.xs),
      horizontalArrangement = Arrangement.spacedBy(JumpSpacing.xs),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Rounded.LocalFireDepartment,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
      )
      Text(value, style = MaterialTheme.typography.titleSmall)
      Text(
        label,
        style = MaterialTheme.typography.labelMedium,
        color = colors.onTertiaryFixedVariant,
      )
    }
  }
}

private data class QuestVisual(
  val container: androidx.compose.ui.graphics.Color,
  val content: androidx.compose.ui.graphics.Color,
  val progress: androidx.compose.ui.graphics.Color,
  val icon: ImageVector,
)

@JumpLightDarkPreviews
@Composable
private fun JumpRewardComponentsPreview() {
  JumpComponentPreview {
    JumpXpProgress(label = "Level 8", value = "740 / 1,000 XP", progress = 0.74f)
    JumpStreakBadge(value = "7", label = "day streak")
    JumpRewardCard(
      title = "Workout reward",
      value = "+120 XP",
      supportingText = "Your strongest session this week",
    )
    JumpQuestCard(
      title = "Daily rhythm",
      description = "Complete one jump session",
      progressText = "1 of 1 workouts",
      statusText = "Complete",
      progress = 1f,
      state = JumpQuestState.COMPLETED,
    )
  }
}
