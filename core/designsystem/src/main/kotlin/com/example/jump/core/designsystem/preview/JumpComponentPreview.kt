package com.example.jump.core.designsystem.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jump.core.designsystem.theme.JumpSpacing
import com.example.jump.core.designsystem.theme.JumpTheme

@Composable
internal fun JumpComponentPreview(
  content: @Composable ColumnScope.() -> Unit,
) {
  JumpTheme {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
      Column(
        modifier = Modifier.padding(JumpSpacing.md),
        verticalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
        content = content,
      )
    }
  }
}
