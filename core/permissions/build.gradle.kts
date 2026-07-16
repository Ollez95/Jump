plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.core.permissions" }

dependencies {
  implementation(project(":core:model"))
  implementation(libs.androidx.core.ktx)
  implementation(libs.dagger.hilt.android)
  ksp(libs.dagger.hilt.compiler)
  testImplementation(libs.junit)
}
