package com.example.jump.feature.workout

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.domain.WorkoutCalorieEstimate
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.IntervalType
import com.example.jump.core.model.QuestId
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.WorkoutInterval
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.WorkoutRewardResult
import org.junit.Rule
import org.junit.Test

class WorkoutScreenTest {
  @get:Rule val composeRule = createAndroidComposeRule<ComponentActivity>()

  private val plan = WorkoutPlan(
    id = "test",
    title = "Jump",
    subtitle = "",
    kind = WorkoutKind.CUSTOM,
    intervals = listOf(WorkoutInterval(IntervalType.WORK, 60)),
  )

  @Test
  fun activeWorkoutShowsIntervalProgressAndControls() {
    composeRule.setContent {
      JumpTheme {
        WorkoutScreen(
          state = ActiveWorkoutState(
            plan = plan,
            phase = SessionPhase.ACTIVE,
            intervalRemainingMillis = 45_000,
            detectedJumps = 42,
          ),
          onTogglePause = {},
          onFinish = {},
          onCorrect = {},
          onDone = {},
        )
      }
    }

    composeRule.onNodeWithText("42").assertExists()
    composeRule.onNodeWithContentDescription("25 percent of this interval complete").assertExists()
    composeRule.onNodeWithContentDescription("Pause").assertExists()
    composeRule.onNodeWithText("Finish and save").assertExists()
  }

  @Test
  fun completionShowsSavedRewardStreakQuestAndCalories() {
    val reward = WorkoutRewardResult(
      workoutSessionId = 1,
      baseXp = 75,
      questXp = 25,
      achievementXp = 0,
      totalAwardedXp = 100,
      totalXpAfter = 600,
      levelBefore = 1,
      levelAfter = 2,
      currentStreak = 3,
      completedQuests = setOf(QuestId.DAILY_WORKOUT),
      unlockedAchievements = emptySet(),
      awardedAtEpochMillis = 0,
    )
    composeRule.setContent {
      JumpTheme {
        WorkoutScreen(
          state = ActiveWorkoutState(
            savedSessionId = 1,
            plan = plan,
            phase = SessionPhase.COMPLETED,
            correctedJumps = 500,
          ),
          rewardState = CompletionRewardUiState.Ready(reward),
          calorieEstimate = WorkoutCalorieEstimate(42, 64),
          onTogglePause = {},
          onFinish = {},
          onCorrect = {},
          onDone = {},
        )
      }
    }

    composeRule.onNodeWithText("Workout saved successfully").assertExists()
    composeRule.onNodeWithText("+100 XP").assertExists()
    composeRule.onNodeWithText("3").assertExists()
    composeRule.onNodeWithText("Daily workout").assertExists()
    composeRule.onNodeWithText("42–64").assertExists()
  }

  @Test
  fun narrowLargeTextWorkoutKeepsPrimaryControlsVisible() {
    composeRule.setContent {
      CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 1.5f)) {
        Box(Modifier.width(320.dp).height(900.dp)) {
          JumpTheme {
            WorkoutScreen(
              state = ActiveWorkoutState(
                plan = plan,
                phase = SessionPhase.ACTIVE,
                intervalRemainingMillis = 45_000,
                detectedJumps = 42,
              ),
              onTogglePause = {},
              onFinish = {},
              onCorrect = {},
              onDone = {},
            )
          }
        }
      }
    }

    composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
    composeRule.onNodeWithText("Finish and save").assertIsDisplayed()
  }
}
