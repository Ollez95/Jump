package com.example.jump.feature.workoutsetup

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.CountingMode
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class WorkoutSetupScreenTest {
  @get:Rule val composeRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun controlsUpdateConfigurationAndExposeSelection() {
    var increased = false
    var selected: CountingMode? = null
    composeRule.setContent {
      JumpTheme {
        WorkoutSetupScreen(
          state = WorkoutSetupUiState(),
          onConfigurationChange = { transform ->
            increased = transform(WorkoutSetupUiState().configuration).jumpSeconds >
              WorkoutSetupUiState().configuration.jumpSeconds
          },
          onCountingModeChange = { selected = it },
          onStart = {},
          onBack = {},
        )
      }
    }

    composeRule.onNodeWithContentDescription("Increase Jump duration").performClick()
    composeRule.runOnIdle { assertTrue(increased) }
    composeRule.onNodeWithContentDescription("Pocket. Uses motion sensors while your phone is secured")
      .assertIsSelected()
    composeRule.onNodeWithContentDescription("Camera. Counts your movement while you remain in frame")
      .performClick()
    composeRule.runOnIdle { assertEquals(CountingMode.CAMERA, selected) }
    composeRule.onNodeWithText("Start custom workout").assertExists()
  }
}
