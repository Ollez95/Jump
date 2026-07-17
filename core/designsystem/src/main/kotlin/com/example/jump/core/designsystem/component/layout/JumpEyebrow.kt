package com.example.jump.core.designsystem.component.layout

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpEyebrow(text: String, modifier: Modifier = Modifier) {
  Text(
    text = text.uppercase(),
    modifier = modifier,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.primary,
  )
}

@JumpLightDarkPreviews
@Composable
private fun JumpEyebrowPreview() {
  JumpComponentPreview {
    JumpEyebrow("Today")
    JumpEyebrow("Training insights")
  }
}
