package com.example.jump.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpEmptyState(
  title: String,
  description: String,
  modifier: Modifier = Modifier,
) {
  JumpCard(modifier = modifier) {
    JumpBrandMark(Modifier.size(46.dp))
    Text(title, style = MaterialTheme.typography.titleLarge)
    Text(
      description,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpEmptyStatePreview() {
  JumpComponentPreview {
    JumpEmptyState(
      title = "Your first session starts here",
      description = "Completed workouts will collect here with jumps, pace, and time.",
    )
  }
}
