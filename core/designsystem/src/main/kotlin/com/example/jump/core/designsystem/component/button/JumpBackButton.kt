package com.example.jump.core.designsystem.component.button

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpBackButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  contentDescription: String = "Back",
) {
  IconButton(
    onClick = onClick,
    modifier = modifier.size(48.dp),
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
      contentDescription = contentDescription,
      modifier = Modifier.size(24.dp),
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpBackButtonPreview() {
  JumpComponentPreview {
    Row(verticalAlignment = Alignment.CenterVertically) {
      JumpBackButton(onClick = {})
      Text("Back to history")
    }
  }
}
