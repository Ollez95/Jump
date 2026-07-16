package com.example.jump.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpDestructiveButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val colors = MaterialTheme.colorScheme
  OutlinedButton(
    onClick = onClick,
    modifier = modifier.height(52.dp),
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, colors.error.copy(alpha = 0.65f)),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.error),
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge)
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpDestructiveButtonPreview() {
  JumpComponentPreview {
    JumpDestructiveButton("Delete session", onClick = {}, modifier = Modifier.fillMaxWidth())
    JumpDestructiveButton(
      "Delete unavailable",
      onClick = {},
      modifier = Modifier.fillMaxWidth(),
      enabled = false,
    )
  }
}
