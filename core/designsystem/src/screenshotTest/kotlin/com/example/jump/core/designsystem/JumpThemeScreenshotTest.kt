package com.example.jump.core.designsystem

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.example.jump.core.designsystem.component.button.JumpMomentumButton
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.button.JumpSecondaryButton
import com.example.jump.core.designsystem.component.card.JumpStatCard
import com.example.jump.core.designsystem.component.navigation.JumpNavigationBar
import com.example.jump.core.designsystem.component.navigation.JumpNavigationIcon
import com.example.jump.core.designsystem.component.navigation.JumpNavigationItem
import com.example.jump.core.designsystem.component.reward.JumpQuestCard
import com.example.jump.core.designsystem.component.reward.JumpQuestState
import com.example.jump.core.designsystem.component.reward.JumpRewardCard
import com.example.jump.core.designsystem.component.reward.JumpStreakBadge
import com.example.jump.core.designsystem.component.reward.JumpXpProgress
import com.example.jump.core.designsystem.theme.JumpSpacing
import com.example.jump.core.designsystem.theme.JumpTheme

@PreviewTest
@Preview(
  name = "Momentum light",
  widthDp = 390,
  heightDp = 884,
  uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun JumpThemeLightScreenshot() {
  MomentumComponentSheet(darkTheme = false)
}

@PreviewTest
@Preview(
  name = "Momentum dark",
  widthDp = 390,
  heightDp = 884,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JumpThemeDarkScreenshot() {
  MomentumComponentSheet(darkTheme = true)
}

@PreviewTest
@Preview(
  name = "Momentum 1.5 font scale",
  widthDp = 390,
  heightDp = 884,
  fontScale = 1.5f,
  uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun JumpThemeLargeFontScreenshot() {
  MomentumComponentSheet(darkTheme = false, compact = true)
}

@Composable
private fun MomentumComponentSheet(
  darkTheme: Boolean,
  compact: Boolean = false,
) {
  JumpTheme(darkTheme = darkTheme) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      Column(
        modifier = Modifier.padding(JumpSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(
          if (compact) JumpSpacing.xs else JumpSpacing.sm,
        ),
      ) {
        Text("Momentum", style = MaterialTheme.typography.displaySmall)
        Text(
          "Train with a clear rhythm.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        JumpXpProgress(label = "Level 8", value = "740 / 1,000 XP", progress = 0.74f)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm),
        ) {
          JumpStatCard("Today", "842", "jumps", Modifier.weight(1f), highlighted = true)
          JumpStatCard("Best", "1,420", "jumps", Modifier.weight(1f))
        }
        JumpStreakBadge(value = "7", label = "day streak")
        if (!compact) {
          JumpRewardCard(
            title = "Workout reward",
            value = "+120 XP",
            supportingText = "Your strongest session this week",
          )
        }
        JumpQuestCard(
          title = "Daily rhythm",
          description = "Complete one jump session",
          progressText = "1 of 1 workouts",
          statusText = "Complete",
          progress = 1f,
          state = JumpQuestState.COMPLETED,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(JumpSpacing.sm)) {
          JumpSecondaryButton("Plan", onClick = {}, modifier = Modifier.weight(1f))
          JumpPrimaryButton("Start", onClick = {}, modifier = Modifier.weight(1f))
        }
        JumpMomentumButton("Claim reward", onClick = {}, modifier = Modifier.fillMaxWidth())
        if (!compact) {
          JumpNavigationBar(
            items = listOf(
              JumpNavigationItem("Today", JumpNavigationIcon.TODAY),
              JumpNavigationItem("Activity", JumpNavigationIcon.HISTORY),
              JumpNavigationItem("Profile", JumpNavigationIcon.PROFILE),
            ),
            selectedIndex = 0,
            onItemSelected = {},
          )
        }
      }
    }
  }
}
