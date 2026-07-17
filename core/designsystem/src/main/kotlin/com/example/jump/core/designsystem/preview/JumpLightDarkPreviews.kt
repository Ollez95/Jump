package com.example.jump.core.designsystem.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
  name = "Light",
  group = "Theme",
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  showBackground = true,
  backgroundColor = 0xFFEEFDF4,
  widthDp = 390,
)
@Preview(
  name = "Dark",
  group = "Theme",
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  showBackground = true,
  backgroundColor = 0xFF030A07,
  widthDp = 390,
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class JumpLightDarkPreviews
