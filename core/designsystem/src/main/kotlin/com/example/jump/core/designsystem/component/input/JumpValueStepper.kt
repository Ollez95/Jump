package com.example.jump.core.designsystem.component.input

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpValueStepper(
  title: String,
  value: String,
  description: String,
  onDecrease: () -> Unit,
  onIncrease: () -> Unit,
  modifier: Modifier = Modifier,
  decreaseEnabled: Boolean = true,
  increaseEnabled: Boolean = true,
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
  ) {
    Row(
      Modifier.padding(horizontal = JumpSpacing.md, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
    ) {
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
          description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      OutlinedButton(
        onClick = onDecrease,
        enabled = decreaseEnabled,
        modifier = Modifier.size(44.dp).semantics { contentDescription = "Decrease $title" },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text("−", style = MaterialTheme.typography.titleLarge)
      }
      Text(
        value,
        modifier = Modifier.width(58.dp),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
      )
      OutlinedButton(
        onClick = onIncrease,
        enabled = increaseEnabled,
        modifier = Modifier.size(44.dp).semantics { contentDescription = "Increase $title" },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text("+", style = MaterialTheme.typography.titleLarge)
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpValueStepperPreview() {
  JumpComponentPreview {
    JumpValueStepper(
      title = "Jump time",
      value = "30s",
      description = "Active time in each round",
      onDecrease = {},
      onIncrease = {},
    )
    JumpValueStepper(
      title = "Rounds",
      value = "60",
      description = "Maximum number of sets",
      onDecrease = {},
      onIncrease = {},
      increaseEnabled = false,
    )
  }
}
