package com.example.jump.core.designsystem.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpStatCard(
  title: String,
  value: String,
  unit: String,
  modifier: Modifier = Modifier,
  highlighted: Boolean = false,
) {
  val colors = MaterialTheme.colorScheme
  val contentColor = if (highlighted) colors.onPrimaryContainer else colors.onSurface
  val supportingColor = if (highlighted) {
    colors.onPrimaryContainer.copy(alpha = 0.76f)
  } else {
    colors.onSurfaceVariant
  }
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (highlighted) colors.primaryContainer else colors.surfaceContainerLow,
      contentColor = contentColor,
    ),
    border = BorderStroke(
      1.dp,
      if (highlighted) {
        colors.primary.copy(alpha = 0.35f)
      } else {
        colors.outlineVariant
      },
    ),
  ) {
    Column(
      Modifier.padding(JumpSpacing.md),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.xxs),
    ) {
      Text(
        title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = supportingColor,
      )
      Text(value, style = MaterialTheme.typography.displaySmall)
      Text(unit, style = MaterialTheme.typography.bodyMedium, color = supportingColor)
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpStatCardPreview() {
  JumpComponentPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      JumpStatCard("Current streak", "7", "days", Modifier.weight(1f), highlighted = true)
      JumpStatCard("Longest streak", "14", "days", Modifier.weight(1f))
    }
  }
}
