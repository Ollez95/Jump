plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.core.workout" }

dependencies {
  implementation(project(":core:model"))
  implementation(project(":core:domain"))
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.service)
  implementation(libs.dagger.hilt.android)
  implementation(libs.kotlinx.coroutines.core)
  ksp(libs.dagger.hilt.compiler)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
