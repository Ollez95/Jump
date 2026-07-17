package com.example.jump.core.designsystem.component.indicator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpProgress(
  progress: Float,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary,
  trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
) {
  val normalizedProgress = progress.coerceIn(0f, 1f)
  val animatedProgress by animateFloatAsState(
    normalizedProgress,
    label = "jump-progress",
  )
  Box(
    modifier
      .fillMaxWidth()
      .height(9.dp)
      .clip(CircleShape)
      .background(trackColor)
      .semantics {
        progressBarRangeInfo = ProgressBarRangeInfo(normalizedProgress, 0f..1f)
      },
  ) {
    Box(
      Modifier
        .fillMaxWidth(animatedProgress)
        .height(9.dp)
        .clip(CircleShape)
        .background(color),
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpProgressPreview() {
  JumpComponentPreview {
    JumpProgress(0f)
    JumpProgress(0.45f)
    JumpProgress(1f)
  }
}
