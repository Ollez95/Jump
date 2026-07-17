package com.example.jump.core.designsystem.component.indicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpMetric(
  label: String,
  value: String,
  unit: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    Text(
      label.uppercase(),
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.primary,
    )
    Text(value, style = MaterialTheme.typography.titleLarge)
    Text(
      unit,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpMetricPreview() {
  JumpComponentPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
      JumpMetric("Pace", "124", "jpm", Modifier.weight(1f))
      JumpMetric("Streak", "38", "best", Modifier.weight(1f))
      JumpMetric("Time", "4:20", "elapsed", Modifier.weight(1f))
    }
  }
}
