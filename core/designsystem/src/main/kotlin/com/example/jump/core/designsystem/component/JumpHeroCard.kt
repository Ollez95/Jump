package com.example.jump.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    shape = MaterialTheme.shapes.extraLarge,
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Box(
      Modifier.background(
        Brush.linearGradient(
          listOf(colors.primaryContainer, colors.secondaryContainer.copy(alpha = 0.88f)),
        ),
      ),
    ) {
      Canvas(Modifier.matchParentSize()) {
        drawCircle(
          colors.onPrimaryContainer.copy(alpha = 0.055f),
          radius = size.minDimension * 0.55f,
          center = center.copy(x = size.width * 0.95f, y = size.height * 0.15f),
        )
        drawCircle(
          colors.onPrimaryContainer.copy(alpha = 0.045f),
          radius = size.minDimension * 0.34f,
          center = center.copy(x = size.width * 0.05f, y = size.height * 0.92f),
        )
      }
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
        Text(title, style = MaterialTheme.typography.headlineMedium, color = colors.onPrimaryContainer)
        Text(
          description,
          style = MaterialTheme.typography.bodyLarge,
          color = colors.onPrimaryContainer.copy(alpha = 0.76f),
        )
        Spacer(Modifier.height(JumpSpacing.xs))
        JumpPrimaryButton(actionLabel, onAction, Modifier.fillMaxWidth(), enabled)
      }
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
