plugins {
  id("jump.android.library")
  id("jump.android.compose")
  alias(libs.plugins.screenshot)
}

android {
  namespace = "com.example.jump.core.designsystem"
  experimentalProperties["android.experimental.enableScreenshotTest"] = true
  testOptions {
    screenshotTests {
      imageDifferenceThreshold = 0.0001f
    }
  }
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.ui.tooling.preview)
  debugImplementation(libs.androidx.compose.ui.tooling)
  screenshotTestImplementation(libs.screenshot.validation.api)
  screenshotTestImplementation(libs.androidx.compose.ui.tooling)
}
