package com.example.jump.core.designsystem.component.button

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

/** High-emphasis reward action reserved for XP, streak, quest, and achievement flows. */
@Composable
fun JumpMomentumButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val colors = MaterialTheme.colorScheme
  Button(
    onClick = onClick,
    modifier = modifier.height(56.dp),
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    colors = ButtonDefaults.buttonColors(
      containerColor = colors.tertiaryFixedDim,
      contentColor = colors.onTertiaryFixed,
      disabledContainerColor = colors.tertiaryFixedDim.copy(alpha = 0.38f),
      disabledContentColor = colors.onTertiaryFixed.copy(alpha = 0.38f),
    ),
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge)
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpMomentumButtonPreview() {
  JumpComponentPreview {
    JumpMomentumButton(label = "Claim reward", onClick = {})
    JumpMomentumButton(label = "Reward claimed", onClick = {}, enabled = false)
  }
}
