plugins {
  id("jump.android.library")
  id("jump.android.compose")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.feature.workout" }

dependencies {
  implementation(project(":core:model"))
  implementation(project(":core:workout"))
  implementation(project(":core:designsystem"))
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
  implementation(libs.dagger.hilt.android)
  ksp(libs.dagger.hilt.compiler)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.test.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
