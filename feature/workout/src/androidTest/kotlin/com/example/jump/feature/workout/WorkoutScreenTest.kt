package com.example.jump.feature.workout

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.ActiveWorkoutState
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.WorkoutKind
import com.example.jump.core.model.WorkoutPlan
import org.junit.Rule
import org.junit.Test

class WorkoutScreenTest {
  @get:Rule val composeRule = createAndroidComposeRule<ComponentActivity>()
  @Test fun activeWorkoutShowsCountAndControls() {
    composeRule.setContent {
      JumpTheme { WorkoutScreen(ActiveWorkoutState(plan = WorkoutPlan("quick", "Quick jump", "", WorkoutKind.QUICK), phase = SessionPhase.ACTIVE, detectedJumps = 42), {}, {}, {}, {}) }
    }
    composeRule.onNodeWithText("42").assertExists(); composeRule.onNodeWithText("Pause").assertExists(); composeRule.onNodeWithText("Finish and save").assertExists()
  }
}
