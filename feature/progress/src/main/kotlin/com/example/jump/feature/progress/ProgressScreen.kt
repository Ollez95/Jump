package com.example.jump.feature.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.layout.JumpDetailRow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.component.navigation.JumpTopAppBar
import com.example.jump.core.domain.TrainingDay
import com.example.jump.core.domain.TrainingWeek
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressRoute(
  onBack: () -> Unit,
  viewModel: ProgressViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  ProgressScreen(state, onBack)
}

@Composable
fun ProgressScreen(state: ProgressUiState, onBack: () -> Unit) {
  val report = state.report
  JumpScreen(
    topBar = {
      JumpTopAppBar(
        title = "Progress",
        onBack = onBack,
        backContentDescription = "Back to history",
      )
    },
  ) {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = stringResource(R.string.progress_eyebrow),
          title = "Your progress",
          description = stringResource(R.string.progress_description),
        )
      }
      if (report == null) {
        item { JumpInfoBanner("Building your insights", "Your training history is being analyzed.") }
      } else {
        if (report.totalSessions == 0) {
          item { JumpInfoBanner("No training activity yet", "Complete a jump session and your calendar and charts will begin to fill in.") }
        }
        item {
          JumpCard {
            Text(stringResource(R.string.progress_overview), style = MaterialTheme.typography.titleLarge)
            JumpDetailRow(stringResource(R.string.progress_current_streak), stringResource(R.string.progress_days, report.currentStreakDays))
            JumpDetailRow(stringResource(R.string.progress_longest_streak), stringResource(R.string.progress_days, report.longestStreakDays))
            JumpDetailRow(stringResource(R.string.progress_sessions), "${report.totalSessions}")
            JumpDetailRow(stringResource(R.string.progress_jumps), "${report.totalJumps}")
            JumpDetailRow(stringResource(R.string.progress_active_time), formatActiveTime(report.totalActiveMillis))
            JumpDetailRow(
              stringResource(R.string.progress_estimated_calories),
              stringResource(
                R.string.progress_calories,
                report.calorieEstimate.minimumCalories,
                report.calorieEstimate.maximumCalories,
              ),
            )
          }
        }
        item {
          JumpCard {
            Text("Training calendar", style = MaterialTheme.typography.titleLarge)
            Text("Last 13 weeks", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TrainingCalendar(report.calendarDays)
            ActivityLegend()
            JumpDetailRow("Days in this period", "${report.calendarDays.count { it.trained }}")
          }
        }
        item {
          JumpCard {
            Text("Weekly activity", style = MaterialTheme.typography.titleLarge)
            Text("Active jump time across the last 8 weeks", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            WeeklyActivityChart(report.weeklyActivity)
          }
        }
        item {
          Text(
            stringResource(R.string.progress_calorie_disclaimer, report.calorieEstimate.referenceWeightKg),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun TrainingCalendar(days: List<TrainingDay>) {
  val weeks = days.chunked(7)
  val labels = listOf("M", "T", "W", "T", "F", "S", "S")
  Row(
    Modifier.fillMaxWidth().padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.Top,
  ) {
    Column(Modifier.width(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      labels.forEach { label ->
        Box(Modifier.size(14.dp), contentAlignment = Alignment.Center) {
          Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      weeks.forEach { week ->
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          week.forEach { day -> TrainingDayCell(day) }
        }
      }
    }
  }
}

@Composable
private fun TrainingDayCell(day: TrainingDay) {
  val color = if (day.isFuture) {
    Color.Transparent
  } else {
    when (day.activityLevel()) {
      0 -> MaterialTheme.colorScheme.surfaceContainerHighest
      1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
      2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.52f)
      3 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.76f)
      else -> MaterialTheme.colorScheme.primary
    }
  }
  val date = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date(day.dayStartEpochMillis))
  val description = when {
    day.isFuture -> "$date, future date"
    day.trained -> "$date, ${day.sessionCount} sessions, ${day.activeMillis / 60_000L} active minutes"
    else -> "$date, no training"
  }
  Box(
    Modifier
      .size(14.dp)
      .clip(RoundedCornerShape(3.dp))
      .background(color)
      .semantics { contentDescription = description },
  )
}

@Composable
private fun ActivityLegend() {
  Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.End,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text("Less", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    listOf(0.0f, 0.28f, 0.52f, 0.76f, 1f).forEachIndexed { index, alpha ->
      Box(
        Modifier
          .padding(start = 4.dp)
          .size(12.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(if (index == 0) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.primary.copy(alpha = alpha)),
      )
    }
    Text("More", Modifier.padding(start = 5.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
private fun WeeklyActivityChart(weeks: List<TrainingWeek>) {
  val maxActiveMillis = weeks.maxOfOrNull { it.activeMillis }?.coerceAtLeast(1L) ?: 1L
  Row(
    Modifier.fillMaxWidth().height(150.dp).padding(top = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(5.dp),
    verticalAlignment = Alignment.Bottom,
  ) {
    weeks.forEach { week ->
      val fraction = (week.activeMillis.toFloat() / maxActiveMillis).coerceIn(0f, 1f)
      Column(
        Modifier.weight(1f).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(if (week.sessionCount > 0) "${week.sessionCount}" else "·", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
          if (week.activeMillis > 0) {
            Box(
              Modifier
                .fillMaxWidth(0.58f)
                .fillMaxHeight(fraction.coerceAtLeast(0.06f))
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(MaterialTheme.colorScheme.primary),
            )
          }
        }
        Text(formatWeek(week.weekStartEpochMillis), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

private fun TrainingDay.activityLevel(): Int = when {
  !trained -> 0
  activeMillis < 5 * 60_000L -> 1
  activeMillis < 15 * 60_000L -> 2
  activeMillis < 30 * 60_000L -> 3
  else -> 4
}

private fun formatWeek(epochMillis: Long): String =
  SimpleDateFormat("d/M", Locale.getDefault()).format(Date(epochMillis))

private fun formatActiveTime(millis: Long): String {
  val minutes = millis / 60_000L
  if (minutes < 60) return "${minutes}m"
  return "${minutes / 60}h ${minutes % 60}m"
}
