plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.core.account" }

dependencies {
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.dagger.hilt.android)
  implementation(libs.kotlinx.coroutines.core)
  ksp(libs.dagger.hilt.compiler)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
