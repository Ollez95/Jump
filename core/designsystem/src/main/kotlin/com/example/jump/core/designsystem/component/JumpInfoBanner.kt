package com.example.jump.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpInfoBanner(
  title: String,
  message: String,
  modifier: Modifier = Modifier,
  isError: Boolean = false,
) {
  val colors = MaterialTheme.colorScheme
  val container = if (isError) colors.errorContainer else colors.secondaryContainer
  val content = if (isError) colors.onErrorContainer else colors.onSecondaryContainer
  Surface(
    modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    color = container,
    contentColor = content,
  ) {
    Column(
      Modifier.padding(JumpSpacing.md),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.xxs),
    ) {
      Text(title, style = MaterialTheme.typography.titleMedium)
      Text(message, style = MaterialTheme.typography.bodyMedium, color = content.copy(alpha = 0.8f))
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpInfoBannerPreview() {
  JumpComponentPreview {
    JumpInfoBanner(
      title = "Camera positioning",
      message = "Keep your full body visible for reliable jump counting.",
    )
    JumpInfoBanner(
      title = "Camera access needed",
      message = "Allow camera access before starting this workout.",
      isError = true,
    )
  }
}
