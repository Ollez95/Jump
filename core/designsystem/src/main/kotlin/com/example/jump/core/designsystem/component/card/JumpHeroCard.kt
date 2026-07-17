package com.example.jump.core.designsystem.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.indicator.JumpBadge
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpHeroCard(
  eyebrow: String,
  title: String,
  description: String,
  actionLabel: String,
  onAction: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  meta: String? = null,
) {
  val colors = MaterialTheme.colorScheme
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Column(
      Modifier.padding(JumpSpacing.xl),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
    ) {
      Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        JumpEyebrow(eyebrow)
        meta?.let { JumpBadge(it, accent = colors.onPrimaryContainer) }
      }
      Text(title, style = MaterialTheme.typography.headlineSmall, color = colors.onPrimaryContainer)
      Text(
        description,
        style = MaterialTheme.typography.bodyMedium,
        color = colors.onPrimaryContainer.copy(alpha = 0.76f),
      )
      Spacer(Modifier.height(JumpSpacing.xxs))
      JumpPrimaryButton(actionLabel, onAction, Modifier.fillMaxWidth(), enabled)
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpHeroCardPreview() {
  JumpComponentPreview {
    JumpHeroCard(
      eyebrow = "Adaptive daily",
      title = "Find your rhythm",
      description = "A balanced workout built around your recent sessions.",
      actionLabel = "Start workout",
      onAction = {},
      meta = "8 MIN",
    )
    JumpHeroCard(
      eyebrow = "Recovery",
      title = "Workout unavailable",
      description = "Finish your active workout before starting another.",
      actionLabel = "Start workout",
      onAction = {},
      enabled = false,
    )
  }
}
