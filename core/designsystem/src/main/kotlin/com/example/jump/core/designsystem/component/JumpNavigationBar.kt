package com.example.jump.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    tonalElevation = 3.dp,
    shadowElevation = 8.dp,
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
        val foreground = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        Column(
          Modifier
            .weight(1f)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(18.dp))
            .selectable(selected = selected, onClick = { onItemSelected(index) }, role = Role.Tab)
            .padding(vertical = 9.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
          JumpNavigationGlyph(item.icon, foreground, Modifier.size(21.dp))
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
    JumpNavigationItem("Settings", JumpNavigationIcon.SETTINGS),
  )
  JumpComponentPreview {
    JumpNavigationBar(items = items, selectedIndex = 0, onItemSelected = {})
    JumpNavigationBar(items = items, selectedIndex = 1, onItemSelected = {})
  }
}

@Composable
private fun JumpNavigationGlyph(icon: JumpNavigationIcon, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
  val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
  Canvas(modifier) {
    val stroke = 1.8.dp.toPx()
    when (icon) {
      JumpNavigationIcon.TODAY -> {
        val path = Path().apply {
          moveTo(size.width * .14f, size.height * .47f)
          lineTo(size.width * .5f, size.height * .17f)
          lineTo(size.width * .86f, size.height * .47f)
          lineTo(size.width * .78f, size.height * .84f)
          lineTo(size.width * .22f, size.height * .84f)
          close()
        }
        drawPath(path, color, style = Stroke(stroke, cap = StrokeCap.Round))
      }
      JumpNavigationIcon.HISTORY -> {
        drawCircle(color, radius = size.minDimension * .34f, style = Stroke(stroke))
        drawLine(color, center, center.copy(y = size.height * .28f), stroke, StrokeCap.Round)
        drawLine(color, center, center.copy(x = size.width * .68f, y = size.height * .58f), stroke, StrokeCap.Round)
      }
      JumpNavigationIcon.SETTINGS -> {
        val ys = listOf(.25f, .5f, .75f)
        val xs = listOf(.38f, .65f, .45f)
        ys.forEachIndexed { index, y ->
          drawLine(color, start = center.copy(x = size.width * .14f, y = size.height * y), end = center.copy(x = size.width * .86f, y = size.height * y), strokeWidth = stroke, cap = StrokeCap.Round)
          drawCircle(surfaceColor, radius = 3.2.dp.toPx(), center = center.copy(x = size.width * xs[index], y = size.height * y))
          drawCircle(color, radius = 3.2.dp.toPx(), center = center.copy(x = size.width * xs[index], y = size.height * y), style = Stroke(stroke))
        }
      }
    }
  }
}
