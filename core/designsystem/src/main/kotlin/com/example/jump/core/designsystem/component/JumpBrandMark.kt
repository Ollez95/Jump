package com.example.jump.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpBrandMark(modifier: Modifier = Modifier) {
  val primary = MaterialTheme.colorScheme.primary
  val container = MaterialTheme.colorScheme.primaryContainer
  Box(
    modifier.clip(CircleShape).background(container),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(Modifier.fillMaxSize().padding(10.dp)) {
      val leftRopeEnd = center.copy(x = size.width * 0.29f, y = size.height * 0.34f)
      val rightRopeEnd = center.copy(x = size.width * 0.71f, y = size.height * 0.34f)
      val rope = Path().apply {
        moveTo(leftRopeEnd.x, leftRopeEnd.y)
        cubicTo(
          size.width * 0.12f,
          size.height * 0.52f,
          size.width * 0.23f,
          size.height * 0.84f,
          size.width * 0.50f,
          size.height * 0.84f,
        )
        cubicTo(
          size.width * 0.77f,
          size.height * 0.84f,
          size.width * 0.88f,
          size.height * 0.52f,
          rightRopeEnd.x,
          rightRopeEnd.y,
        )
      }
      drawPath(rope, primary, style = Stroke(2.2.dp.toPx(), cap = StrokeCap.Round))
      drawLine(
        primary,
        start = leftRopeEnd,
        end = center.copy(x = size.width * 0.18f, y = size.height * 0.12f),
        strokeWidth = 5.dp.toPx(),
        cap = StrokeCap.Round,
      )
      drawLine(
        primary,
        start = rightRopeEnd,
        end = center.copy(x = size.width * 0.82f, y = size.height * 0.12f),
        strokeWidth = 5.dp.toPx(),
        cap = StrokeCap.Round,
      )
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpBrandMarkPreview() {
  JumpComponentPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
      JumpBrandMark(Modifier.size(40.dp))
      JumpBrandMark(Modifier.size(64.dp))
      JumpBrandMark(Modifier.size(96.dp))
    }
  }
}
