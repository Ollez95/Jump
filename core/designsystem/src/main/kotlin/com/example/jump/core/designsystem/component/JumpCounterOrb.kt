package com.example.jump.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpCounterOrb(
  value: String,
  label: String,
  modifier: Modifier = Modifier,
) {
  val colors = MaterialTheme.colorScheme
  Box(
    modifier
      .size(244.dp)
      .clip(CircleShape)
      .background(
        Brush.radialGradient(
          listOf(colors.primaryContainer, colors.surfaceContainerHigh),
        ),
      )
      .border(1.dp, colors.primary.copy(alpha = 0.4f), CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(Modifier.fillMaxSize()) {
      drawCircle(
        colors.primary.copy(alpha = 0.08f),
        radius = size.minDimension * 0.44f,
        style = Stroke(width = 2.dp.toPx()),
      )
      drawArc(
        colors.primary.copy(alpha = 0.7f),
        -90f,
        235f,
        false,
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
      )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(value, style = MaterialTheme.typography.displayLarge, color = colors.onPrimaryContainer)
      Text(
        label.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = colors.onPrimaryContainer,
        letterSpacing = 2.sp,
      )
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpCounterOrbPreview() {
  JumpComponentPreview {
    JumpCounterOrb(value = "248", label = "Jumps")
  }
}
