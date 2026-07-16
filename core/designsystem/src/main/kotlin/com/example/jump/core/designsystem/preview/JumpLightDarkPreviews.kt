package com.example.jump.core.designsystem.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
  name = "Light",
  group = "Theme",
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  showBackground = true,
  backgroundColor = 0xFFF7FAF6,
  widthDp = 360,
)
@Preview(
  name = "Dark",
  group = "Theme",
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  showBackground = true,
  backgroundColor = 0xFF07130E,
  widthDp = 360,
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class JumpLightDarkPreviews
