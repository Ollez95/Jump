package com.example.jump.core.designsystem.component.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpPrimaryButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  Button(
    onClick = onClick,
    modifier = modifier.height(54.dp),
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    contentPadding = ButtonDefaults.ContentPadding,
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge)
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpPrimaryButtonPreview() {
  JumpComponentPreview {
    JumpPrimaryButton("Start workout", onClick = {}, modifier = Modifier.fillMaxWidth())
    JumpPrimaryButton(
      "Workout in progress",
      onClick = {},
      modifier = Modifier.fillMaxWidth(),
      enabled = false,
    )
  }
}
