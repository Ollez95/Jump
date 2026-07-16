package com.example.jump.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpSecondaryButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier.height(52.dp),
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge)
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpSecondaryButtonPreview() {
  JumpComponentPreview {
    JumpSecondaryButton("Configure workout", onClick = {}, modifier = Modifier.fillMaxWidth())
    JumpSecondaryButton(
      "Unavailable",
      onClick = {},
      modifier = Modifier.fillMaxWidth(),
      enabled = false,
    )
  }
}
