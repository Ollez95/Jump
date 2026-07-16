package com.example.jump.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpChoiceCard(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  description: String? = null,
) {
  val colors = MaterialTheme.colorScheme
  Surface(
    onClick = onClick,
    modifier = modifier.fillMaxWidth().semantics { this.selected = selected },
    shape = MaterialTheme.shapes.medium,
    color = if (selected) colors.primaryContainer else colors.surfaceContainerLow,
    contentColor = if (selected) colors.onPrimaryContainer else colors.onSurface,
    border = BorderStroke(
      1.dp,
      if (selected) colors.primary else colors.outlineVariant.copy(alpha = 0.7f),
    ),
  ) {
    Row(
      Modifier.padding(horizontal = JumpSpacing.md, vertical = 15.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        Modifier
          .size(20.dp)
          .clip(CircleShape)
          .border(1.5.dp, if (selected) colors.primary else colors.outline, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        if (selected) {
          Box(Modifier.size(10.dp).clip(CircleShape).background(colors.primary))
        }
      }
      Spacer(Modifier.width(JumpSpacing.sm))
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        description?.let {
          Text(it, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        }
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpChoiceCardPreview() {
  JumpComponentPreview {
    JumpChoiceCard(
      label = "Camera tracking",
      description = "Count jumps while your body remains in frame.",
      selected = true,
      onClick = {},
    )
    JumpChoiceCard(
      label = "Pocket motion",
      description = "Count using the phone's motion sensors.",
      selected = false,
      onClick = {},
    )
  }
}
