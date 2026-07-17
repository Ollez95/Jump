plugins {
  id("jump.android.library")
  id("jump.android.compose")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.feature.workoutsetup" }

dependencies {
  implementation(project(":core:model"))
  implementation(project(":core:domain"))
  implementation(project(":core:designsystem"))
  implementation(project(":core:workout"))
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
  implementation(libs.dagger.hilt.android)
  ksp(libs.dagger.hilt.compiler)
  androidTestImplementation(composeBom)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
