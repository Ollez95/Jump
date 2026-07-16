package com.example.jump.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jump.core.designsystem.theme.JumpSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JumpScreen(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val colors = MaterialTheme.colorScheme
  CompositionLocalProvider(LocalContentColor provides colors.onBackground) {
    Box(
      modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              colors.background,
              colors.surfaceContainerLow.copy(alpha = 0.7f),
              colors.background,
            ),
          ),
        ),
    ) {
      content()
    }
  }
}

@Composable
fun JumpHeader(
  title: String,
  modifier: Modifier = Modifier,
  eyebrow: String? = null,
  description: String? = null,
  brandMark: Boolean = false,
) {
  Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(JumpSpacing.xs)) {
      eyebrow?.let { JumpEyebrow(it) }
      Text(title, style = MaterialTheme.typography.headlineLarge)
      description?.let {
        Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
    if (brandMark) {
      Spacer(Modifier.width(JumpSpacing.md))
      JumpBrandMark(Modifier.size(52.dp))
    }
  }
}

@Composable
fun JumpEyebrow(text: String, modifier: Modifier = Modifier) {
  Text(
    text = text.uppercase(),
    modifier = modifier,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.primary,
  )
}

@Composable
fun JumpBadge(text: String, modifier: Modifier = Modifier, accent: Color = MaterialTheme.colorScheme.primary) {
  Surface(
    modifier = modifier,
    shape = CircleShape,
    color = accent.copy(alpha = 0.13f),
    contentColor = accent,
  ) {
    Text(text, Modifier.padding(horizontal = 11.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium)
  }
}

@Composable
fun JumpCard(
  modifier: Modifier = Modifier,
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
  content: @Composable ColumnScope.() -> Unit,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Column(Modifier.padding(JumpSpacing.lg), verticalArrangement = Arrangement.spacedBy(JumpSpacing.sm), content = content)
  }
}

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
        drawCircle(colors.onPrimaryContainer.copy(alpha = 0.055f), radius = size.minDimension * 0.55f, center = center.copy(x = size.width * 0.95f, y = size.height * 0.15f))
        drawCircle(colors.onPrimaryContainer.copy(alpha = 0.045f), radius = size.minDimension * 0.34f, center = center.copy(x = size.width * 0.05f, y = size.height * 0.92f))
      }
      Column(Modifier.padding(JumpSpacing.xl), verticalArrangement = Arrangement.spacedBy(JumpSpacing.sm)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          JumpEyebrow(eyebrow)
          meta?.let { JumpBadge(it, accent = colors.onPrimaryContainer) }
        }
        Text(title, style = MaterialTheme.typography.headlineMedium, color = colors.onPrimaryContainer)
        Text(description, style = MaterialTheme.typography.bodyLarge, color = colors.onPrimaryContainer.copy(alpha = 0.76f))
        Spacer(Modifier.height(JumpSpacing.xs))
        JumpPrimaryButton(actionLabel, onAction, Modifier.fillMaxWidth(), enabled)
      }
    }
  }
}

@Composable
fun JumpPrimaryButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  Button(
    onClick = onClick,
    modifier = modifier.height(54.dp),
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    contentPadding = ButtonDefaults.ContentPadding,
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge)
  }
}

@Composable
fun JumpSecondaryButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier.height(52.dp),
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge)
  }
}

@Composable
fun JumpChoiceCard(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  description: String? = null,
) {
  val colors = MaterialTheme.colorScheme
  Surface(
    onClick = onClick,
    modifier = modifier.fillMaxWidth().semantics { this.selected = selected },
    shape = MaterialTheme.shapes.medium,
    color = if (selected) colors.primaryContainer else colors.surfaceContainerLow,
    contentColor = if (selected) colors.onPrimaryContainer else colors.onSurface,
    border = BorderStroke(1.dp, if (selected) colors.primary else colors.outlineVariant.copy(alpha = 0.7f)),
  ) {
    Row(Modifier.padding(horizontal = JumpSpacing.md, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
      Box(
        Modifier
          .size(20.dp)
          .clip(CircleShape)
          .border(1.5.dp, if (selected) colors.primary else colors.outline, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        if (selected) Box(Modifier.size(10.dp).clip(CircleShape).background(colors.primary))
      }
      Spacer(Modifier.width(JumpSpacing.sm))
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant) }
      }
    }
  }
}

@Composable
fun JumpNumberChip(value: Int, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
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

@Composable
fun JumpStatCard(title: String, value: String, unit: String, modifier: Modifier = Modifier, highlighted: Boolean = false) {
  val colors = MaterialTheme.colorScheme
  Card(
    modifier = modifier,
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = if (highlighted) colors.primaryContainer else colors.surfaceContainerLow),
    border = BorderStroke(1.dp, if (highlighted) colors.primary.copy(alpha = 0.35f) else colors.outlineVariant.copy(alpha = 0.55f)),
  ) {
    Column(Modifier.padding(JumpSpacing.md), verticalArrangement = Arrangement.spacedBy(JumpSpacing.xxs)) {
      Text(title.uppercase(), style = MaterialTheme.typography.labelMedium, color = if (highlighted) colors.primary else colors.onSurfaceVariant)
      Text(value, style = MaterialTheme.typography.headlineMedium)
      Text(unit, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
    }
  }
}

@Composable
fun JumpMetric(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
  Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
    Text(value, style = MaterialTheme.typography.titleLarge)
    Text(unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
fun JumpDetailRow(label: String, value: String, modifier: Modifier = Modifier) {
  Row(modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.End)
  }
}

@Composable
fun JumpProgress(progress: Float, modifier: Modifier = Modifier) {
  val animatedProgress by animateFloatAsState(progress.coerceIn(0f, 1f), label = "jump-progress")
  Box(modifier.fillMaxWidth().height(9.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerHighest)) {
    Box(Modifier.fillMaxWidth(animatedProgress).height(9.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
  }
}

@Composable
fun JumpSettingRow(
  title: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(modifier.fillMaxWidth().padding(vertical = JumpSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
    Column(Modifier.weight(1f).padding(end = JumpSpacing.md), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(title, style = MaterialTheme.typography.titleMedium)
      Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

@Composable
fun JumpInfoBanner(title: String, message: String, modifier: Modifier = Modifier, isError: Boolean = false) {
  val colors = MaterialTheme.colorScheme
  val container = if (isError) colors.errorContainer else colors.secondaryContainer
  val content = if (isError) colors.onErrorContainer else colors.onSecondaryContainer
  Surface(modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = container, contentColor = content) {
    Column(Modifier.padding(JumpSpacing.md), verticalArrangement = Arrangement.spacedBy(JumpSpacing.xxs)) {
      Text(title, style = MaterialTheme.typography.titleMedium)
      Text(message, style = MaterialTheme.typography.bodyMedium, color = content.copy(alpha = 0.8f))
    }
  }
}

@Composable
fun JumpEmptyState(title: String, description: String, modifier: Modifier = Modifier) {
  JumpCard(modifier = modifier) {
    JumpBrandMark(Modifier.size(46.dp))
    Text(title, style = MaterialTheme.typography.titleLarge)
    Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
fun JumpCounterOrb(value: String, label: String, modifier: Modifier = Modifier) {
  val colors = MaterialTheme.colorScheme
  Box(
    modifier
      .size(244.dp)
      .clip(CircleShape)
      .background(Brush.radialGradient(listOf(colors.primaryContainer, colors.surfaceContainerHigh)))
      .border(1.dp, colors.primary.copy(alpha = 0.4f), CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(Modifier.fillMaxSize()) {
      drawCircle(colors.primary.copy(alpha = 0.08f), radius = size.minDimension * 0.44f, style = Stroke(width = 2.dp.toPx()))
      drawArc(colors.primary.copy(alpha = 0.7f), -90f, 235f, false, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(value, style = MaterialTheme.typography.displayLarge, color = colors.onPrimaryContainer)
      Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = colors.onPrimaryContainer, letterSpacing = 2.sp)
    }
  }
}

@Composable
fun JumpBrandMark(modifier: Modifier = Modifier) {
  val primary = MaterialTheme.colorScheme.primary
  val container = MaterialTheme.colorScheme.primaryContainer
  Box(modifier.clip(CircleShape).background(container), contentAlignment = Alignment.Center) {
    Canvas(Modifier.fillMaxSize().padding(10.dp)) {
      val stroke = 2.6.dp.toPx()
      drawArc(primary, 205f, 130f, false, style = Stroke(stroke, cap = StrokeCap.Round))
      drawLine(primary, start = center.copy(x = size.width * 0.20f, y = size.height * 0.69f), end = center.copy(x = size.width * 0.32f, y = size.height * 0.86f), strokeWidth = stroke, cap = StrokeCap.Round)
      drawLine(primary, start = center.copy(x = size.width * 0.80f, y = size.height * 0.69f), end = center.copy(x = size.width * 0.68f, y = size.height * 0.86f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
  }
}

fun formatDuration(millis: Long): String {
  val total = (millis / 1_000).coerceAtLeast(0)
  return "%d:%02d".format(total / 60, total % 60)
}

fun formatDate(epochMillis: Long): String =
  SimpleDateFormat("EEE, d MMM • HH:mm", Locale.getDefault()).format(Date(epochMillis))
