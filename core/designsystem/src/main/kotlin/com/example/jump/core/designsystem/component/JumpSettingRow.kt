package com.example.jump.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpSpacing

@Composable
fun JumpSettingRow(
  title: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier.fillMaxWidth().padding(vertical = JumpSpacing.xs),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      Modifier.weight(1f).padding(end = JumpSpacing.md),
      verticalArrangement = Arrangement.spacedBy(JumpSpacing.xxs),
    ) {
      Text(title, style = MaterialTheme.typography.titleMedium)
      Text(
        description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
      ),
    )
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpSettingRowPreview() {
  JumpComponentPreview {
    JumpSettingRow("Voice coaching", "Spoken prompts during intervals", true, {})
    JumpSettingRow("Vibration", "Haptic confirmation for key moments", false, {})
  }
}
