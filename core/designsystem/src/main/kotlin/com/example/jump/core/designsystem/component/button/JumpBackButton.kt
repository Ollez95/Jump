package com.example.jump.core.designsystem.component.button

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpBackButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  contentDescription: String = "Back",
) {
  val iconColor = MaterialTheme.colorScheme.onBackground
  IconButton(
    onClick = onClick,
    modifier = modifier
      .size(48.dp)
      .semantics { this.contentDescription = contentDescription },
  ) {
    Canvas(Modifier.size(24.dp)) {
      val stroke = 2.dp.toPx()
      val start = center.copy(x = size.width * 0.18f)
      val end = center.copy(x = size.width * 0.82f)
      drawLine(iconColor, start, end, stroke, StrokeCap.Round)
      drawLine(
        iconColor,
        start,
        start.copy(x = size.width * 0.43f, y = size.height * 0.25f),
        stroke,
        StrokeCap.Round,
      )
      drawLine(
        iconColor,
        start,
        start.copy(x = size.width * 0.43f, y = size.height * 0.75f),
        stroke,
        StrokeCap.Round,
      )
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpBackButtonPreview() {
  JumpComponentPreview {
    Row(verticalAlignment = Alignment.CenterVertically) {
      JumpBackButton(onClick = {})
      Text("Back to history")
    }
  }
}
