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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpStatCard
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.layout.JumpDetailRow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.component.navigation.JumpTopAppBar
import com.example.jump.core.designsystem.component.reward.JumpQuestCard
import com.example.jump.core.designsystem.component.reward.JumpQuestState
import com.example.jump.core.designsystem.component.reward.JumpStreakBadge
import com.example.jump.core.designsystem.component.reward.JumpXpProgress
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.domain.TrainingDay
import com.example.jump.core.domain.TrainingWeek
import com.example.jump.core.model.AchievementId
import com.example.jump.core.model.QuestId
import com.example.jump.core.model.QuestProgress
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
  val locale = LocalConfiguration.current.locales[0]
  JumpScreen(
    topBar = {
      JumpTopAppBar(
        title = stringResource(R.string.progress_title),
        onBack = onBack,
        backContentDescription = stringResource(R.string.progress_back),
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
          title = stringResource(R.string.progress_your_progress),
          description = stringResource(R.string.progress_description),
        )
      }
      if (report == null) {
        item {
          JumpInfoBanner(
            if (state.loadFailed) stringResource(R.string.progress_error_title)
            else stringResource(R.string.progress_loading_title),
            if (state.loadFailed) stringResource(R.string.progress_error_message)
            else stringResource(R.string.progress_loading_message),
            isError = state.loadFailed,
          )
        }
      } else {
        if (report.totalSessions == 0) {
          item {
            JumpInfoBanner(
              stringResource(R.string.progress_empty_title),
              stringResource(R.string.progress_empty_message),
            )
          }
        }
        state.rewards?.let { rewards ->
          item {
            JumpCard {
              Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  JumpEyebrow(stringResource(R.string.progress_momentum))
                  Text(
                    stringResource(R.string.progress_level, rewards.level),
                    style = MaterialTheme.typography.headlineMedium,
                  )
                }
                JumpStreakBadge(
                  value = rewards.currentStreak.toString(),
                  label = stringResource(R.string.progress_day_streak),
                )
              }
              JumpXpProgress(
                label = stringResource(R.string.progress_next_level),
                value = stringResource(
                  R.string.progress_xp_value,
                  rewards.xpInLevel,
                  rewards.xpToNextLevel,
                ),
                progress = rewards.xpInLevel.toFloat() / rewards.xpToNextLevel.coerceAtLeast(1),
              )
            }
          }
        }
        item {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            JumpStatCard(
              stringResource(R.string.progress_jumps),
              report.totalJumps.toString(),
              stringResource(R.string.progress_total),
              Modifier.weight(1f),
              highlighted = true,
            )
            JumpStatCard(
              stringResource(R.string.progress_sessions),
              report.totalSessions.toString(),
              stringResource(R.string.progress_workouts),
              Modifier.weight(1f),
            )
          }
        }
        item {
          JumpCard {
            Text(stringResource(R.string.progress_overview), style = MaterialTheme.typography.titleLarge)
            JumpDetailRow(stringResource(R.string.progress_current_streak), stringResource(R.string.progress_days, report.currentStreakDays))
            JumpDetailRow(stringResource(R.string.progress_longest_streak), stringResource(R.string.progress_days, report.longestStreakDays))
            JumpDetailRow(stringResource(R.string.progress_active_time), formatActiveTime(report.totalActiveMillis))
            JumpDetailRow(
              stringResource(R.string.progress_estimated_calories),
              stringResource(R.string.progress_calories, report.calorieEstimate.minimumCalories, report.calorieEstimate.maximumCalories),
            )
          }
        }
        state.rewards?.let { rewards ->
          item { JumpEyebrow(stringResource(R.string.progress_quest_history)) }
          items(rewards.dailyQuests + rewards.weeklyQuests, key = { it.id.name }) { quest ->
            QuestProgressCard(quest)
          }
          item {
            JumpCard {
              Text(stringResource(R.string.progress_achievements), style = MaterialTheme.typography.titleLarge)
              Text(
                stringResource(
                  R.string.progress_achievements_summary,
                  rewards.achievements.size,
                  AchievementId.entries.size,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              AchievementId.entries.forEach { achievement ->
                val unlock = rewards.achievements.firstOrNull { it.id == achievement }
                Column(
                  Modifier.fillMaxWidth().padding(vertical = 4.dp),
                  verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                  Text(achievementLabel(achievement), style = MaterialTheme.typography.titleMedium)
                  Text(
                    unlock?.let {
                    stringResource(R.string.progress_unlocked, formatAchievementDate(it.unlockedAtEpochMillis, locale))
                    } ?: stringResource(R.string.progress_locked),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (unlock == null) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.primary,
                  )
                }
              }
            }
          }
        }
        item {
          JumpCard {
            Text(stringResource(R.string.progress_training_calendar), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.progress_last_weeks), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TrainingCalendar(report.calendarDays)
            ActivityLegend()
            JumpDetailRow(stringResource(R.string.progress_days_period), "${report.calendarDays.count { it.trained }}")
          }
        }
        item {
          JumpCard {
            Text(stringResource(R.string.progress_weekly_activity), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.progress_weekly_activity_description), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun QuestProgressCard(quest: QuestProgress) {
  JumpQuestCard(
    title = questLabel(quest.id),
    description = questDescription(quest.id),
    progressText = stringResource(
      R.string.progress_quest_value,
      quest.progress.coerceAtMost(quest.id.target),
      quest.id.target,
    ),
    statusText = if (quest.completed) {
      stringResource(R.string.progress_quest_complete)
    } else {
      stringResource(R.string.progress_quest_active)
    },
    progress = quest.progress.toFloat() / quest.id.target.coerceAtLeast(1),
    state = if (quest.completed) JumpQuestState.COMPLETED else JumpQuestState.IN_PROGRESS,
  )
}

@Composable
private fun questLabel(id: QuestId): String = stringResource(
  when (id) {
    QuestId.DAILY_WORKOUT -> R.string.quest_daily_workout
    QuestId.DAILY_JUMPS -> R.string.quest_daily_jumps
    QuestId.WEEKLY_WORKOUTS -> R.string.quest_weekly_workouts
    QuestId.WEEKLY_JUMPS -> R.string.quest_weekly_jumps
  },
)

@Composable
private fun questDescription(id: QuestId): String = stringResource(
  when (id) {
    QuestId.DAILY_WORKOUT -> R.string.quest_daily_workout_description
    QuestId.DAILY_JUMPS -> R.string.quest_daily_jumps_description
    QuestId.WEEKLY_WORKOUTS -> R.string.quest_weekly_workouts_description
    QuestId.WEEKLY_JUMPS -> R.string.quest_weekly_jumps_description
  },
)

@Composable
private fun achievementLabel(id: AchievementId): String = stringResource(
  when (id) {
    AchievementId.FIRST_WORKOUT -> R.string.achievement_first_workout
    AchievementId.ONE_THOUSAND_TOTAL_JUMPS -> R.string.achievement_thousand_jumps
    AchievementId.THREE_DAY_STREAK -> R.string.achievement_three_day_streak
    AchievementId.ONE_THOUSAND_JUMP_WORKOUT -> R.string.achievement_thousand_workout
    AchievementId.TEN_WORKOUTS -> R.string.achievement_ten_workouts
  },
)

private fun formatAchievementDate(epochMillis: Long, locale: Locale): String =
  SimpleDateFormat("d MMM", locale).format(Date(epochMillis))

@Composable
private fun TrainingCalendar(days: List<TrainingDay>) {
  val weeks = days.chunked(7)
  val labels = stringArrayResource(R.array.progress_weekday_labels)
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
  val date = SimpleDateFormat(
    "EEE, d MMM",
    LocalConfiguration.current.locales[0],
  ).format(Date(day.dayStartEpochMillis))
  val description = when {
    day.isFuture -> stringResource(R.string.progress_future_date, date)
    day.trained -> stringResource(
      R.string.progress_trained_date,
      date,
      day.sessionCount,
      day.activeMillis / 60_000L,
    )
    else -> stringResource(R.string.progress_no_training_date, date)
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
    Text(stringResource(R.string.progress_less), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    listOf(0.0f, 0.28f, 0.52f, 0.76f, 1f).forEachIndexed { index, alpha ->
      Box(
        Modifier
          .padding(start = 4.dp)
          .size(12.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(if (index == 0) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.primary.copy(alpha = alpha)),
      )
    }
    Text(stringResource(R.string.progress_more), Modifier.padding(start = 5.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
private fun WeeklyActivityChart(weeks: List<TrainingWeek>) {
  val maxActiveMillis = weeks.maxOfOrNull { it.activeMillis }?.coerceAtLeast(1L) ?: 1L
  val locale = LocalConfiguration.current.locales[0]
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
        Text(formatWeek(week.weekStartEpochMillis, locale), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

private fun formatWeek(epochMillis: Long, locale: Locale): String =
  SimpleDateFormat("d/M", locale).format(Date(epochMillis))

@Composable
private fun formatActiveTime(millis: Long): String {
  val minutes = millis / 60_000L
  if (minutes < 60) return stringResource(R.string.progress_minutes, minutes)
  return stringResource(R.string.progress_hours_minutes, minutes / 60, minutes % 60)
}

@JumpLightDarkPreviews
@Preview(name = "1.5x font", widthDp = 390, heightDp = 884, fontScale = 1.5f, showBackground = true)
@Composable
private fun ProgressScreenPreview() {
  JumpTheme { ProgressScreen(state = ProgressUiState(), onBack = {}) }
}
