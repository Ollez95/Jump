package com.example.jump.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpBadge(
  text: String,
  modifier: Modifier = Modifier,
  accent: Color = MaterialTheme.colorScheme.primary,
) {
  Surface(
    modifier = modifier,
    shape = CircleShape,
    color = accent.copy(alpha = 0.13f),
    contentColor = accent,
  ) {
    Text(
      text,
      Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
      style = MaterialTheme.typography.labelMedium,
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpBadgePreview() {
  JumpComponentPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      JumpBadge("LIVE")
      JumpBadge("GOAL MET", accent = MaterialTheme.colorScheme.secondary)
      JumpBadge("24 JUMPS", accent = MaterialTheme.colorScheme.tertiary)
    }
  }
}
