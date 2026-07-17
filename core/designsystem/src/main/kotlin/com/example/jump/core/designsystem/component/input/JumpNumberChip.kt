package com.example.jump.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpNumberChip(
  value: Int,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    label = { Text("$value") },
    modifier = modifier.height(44.dp),
    shape = MaterialTheme.shapes.medium,
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = MaterialTheme.colorScheme.primary,
      selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
    ),
    border = FilterChipDefaults.filterChipBorder(
      enabled = true,
      selected = selected,
      borderColor = MaterialTheme.colorScheme.outlineVariant,
      selectedBorderColor = MaterialTheme.colorScheme.primary,
    ),
  )
}

@JumpLightDarkPreviews
@Composable
private fun JumpNumberChipPreview() {
  JumpComponentPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      (2..6).forEach { value ->
        JumpNumberChip(value = value, selected = value == 3, onClick = {})
      }
    }
  }
}
