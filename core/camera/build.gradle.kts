plugins {
  id("jump.android.library")
  id("jump.android.compose")
}

android { namespace = "com.example.jump.core.camera" }

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)
  implementation(libs.google.mlkit.pose.detection)
  testImplementation(libs.junit)
}
