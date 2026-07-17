plugins {
  id("jump.android.library")
  id("jump.android.compose")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.feature.settings" }

dependencies {
  implementation(project(":core:model"))
  implementation(project(":core:domain"))
  implementation(project(":core:account"))
  implementation(project(":core:designsystem"))
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
  implementation(libs.dagger.hilt.android)
  ksp(libs.dagger.hilt.compiler)
  testImplementation(libs.junit)
  testImplementation(libs.truth)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
