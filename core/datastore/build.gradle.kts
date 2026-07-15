plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.core.datastore" }

dependencies {
  implementation(project(":core:model"))
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.dagger.hilt.android)
  ksp(libs.dagger.hilt.compiler)
}
