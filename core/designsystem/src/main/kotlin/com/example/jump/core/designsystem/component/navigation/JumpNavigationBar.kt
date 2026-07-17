package com.example.jump.core.designsystem.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@Composable
fun JumpNavigationBar(
  items: List<JumpNavigationItem>,
  selectedIndex: Int,
  onItemSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surfaceContainer,
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
  ) {
    Row(
      Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
        .padding(horizontal = 10.dp, vertical = 8.dp)
        .selectableGroup(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      items.forEachIndexed { index, item ->
        val selected = selectedIndex == index
        val foreground = if (selected) {
          MaterialTheme.colorScheme.onPrimaryContainer
        } else {
          MaterialTheme.colorScheme.onSurfaceVariant
        }
        Column(
          Modifier
            .weight(1f)
            .selectable(selected = selected, onClick = { onItemSelected(index) }, role = Role.Tab)
            .padding(vertical = 9.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
          Surface(
            shape = CircleShape,
            color = if (selected) {
              MaterialTheme.colorScheme.primaryContainer
            } else {
              MaterialTheme.colorScheme.surfaceContainer
            },
          ) {
            Box(
              modifier = Modifier.size(width = 44.dp, height = 28.dp),
              contentAlignment = Alignment.Center,
            ) {
              JumpNavigationGlyph(item.icon, foreground, Modifier.size(20.dp))
            }
          }
          Text(item.label, style = MaterialTheme.typography.labelMedium, color = foreground)
        }
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpNavigationBarPreview() {
  val items = listOf(
    JumpNavigationItem("Today", JumpNavigationIcon.TODAY),
    JumpNavigationItem("History", JumpNavigationIcon.HISTORY),
    JumpNavigationItem("Profile", JumpNavigationIcon.PROFILE),
  )
  JumpComponentPreview {
    JumpNavigationBar(items = items, selectedIndex = 0, onItemSelected = {})
    JumpNavigationBar(items = items, selectedIndex = 1, onItemSelected = {})
  }
}

@Composable
private fun JumpNavigationGlyph(icon: JumpNavigationIcon, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
  Icon(
    imageVector = icon.imageVector(),
    contentDescription = null,
    tint = color,
    modifier = modifier,
  )
}

private fun JumpNavigationIcon.imageVector(): ImageVector = when (this) {
  JumpNavigationIcon.TODAY -> Icons.Rounded.Home
  JumpNavigationIcon.HISTORY -> Icons.Rounded.History
  JumpNavigationIcon.PROFILE -> Icons.Rounded.Person
}
