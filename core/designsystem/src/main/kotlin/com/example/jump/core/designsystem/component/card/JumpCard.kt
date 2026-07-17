package com.example.jump.core.designsystem.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpCard(
  modifier: Modifier = Modifier,
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
  content: @Composable ColumnScope.() -> Unit,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = containerColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Column(
      Modifier.padding(JumpSpacing.lg),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
      content = content,
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpCardPreview() {
  JumpComponentPreview {
    JumpCard {
      Text("Standard card", style = MaterialTheme.typography.titleLarge)
      Text("Uses the low surface container.")
    }
    JumpCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
      Text("Highlighted card", style = MaterialTheme.typography.titleLarge)
      Text("Uses the primary container.")
    }
  }
}
