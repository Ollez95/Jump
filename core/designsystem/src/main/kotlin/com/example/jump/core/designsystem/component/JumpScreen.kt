package com.example.jump.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme

@Composable
fun JumpScreen(
  modifier: Modifier = Modifier,
  topBar: @Composable () -> Unit = {},
  bottomBar: @Composable () -> Unit = {},
  content: @Composable () -> Unit,
) {
  val colors = MaterialTheme.colorScheme
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
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = topBar,
      bottomBar = bottomBar,
      containerColor = Color.Transparent,
      contentColor = colors.onBackground,
    ) { innerPadding ->
      CompositionLocalProvider(LocalContentColor provides colors.onBackground) {
        Box(Modifier.fillMaxSize().padding(innerPadding)) { content() }
      }
    }
  }
}

@JumpLightDarkPreviews
@Composable
private fun JumpScreenPreview() {
  JumpTheme {
    Box(Modifier.height(360.dp)) {
      JumpScreen(
        topBar = { JumpTopAppBar(title = "Workout", onBack = {}) },
      ) {
        Column(Modifier.padding(20.dp)) {
          JumpHeader(
            eyebrow = "Preview",
            title = "A themed screen",
            description = "Scaffold content uses the correct semantic colors.",
          )
        }
      }
    }
  }
}
