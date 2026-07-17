package com.example.jump.core.designsystem.component.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.component.branding.JumpBrandMark
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpHeader(
  title: String,
  modifier: Modifier = Modifier,
  eyebrow: String? = null,
  description: String? = null,
  brandMark: Boolean = false,
) {
  Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(JumpSpacing.xs)) {
      eyebrow?.let { JumpEyebrow(it) }
      Text(title, style = MaterialTheme.typography.headlineLarge)
      description?.let {
        Text(
          it,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
    if (brandMark) {
      Spacer(Modifier.width(JumpSpacing.md))
      JumpBrandMark(Modifier.size(52.dp))
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpHeaderPreview() {
  JumpComponentPreview {
    JumpHeader(title = "Workout history")
    JumpHeader(
      eyebrow = "Your progress",
      title = "Build momentum",
      description = "Every session is proof of consistency.",
      brandMark = true,
    )
  }
}
