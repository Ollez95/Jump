plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android {
  namespace = "com.example.jump.core.data"
  defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
  implementation(project(":core:model"))
  implementation(project(":core:domain"))
  implementation(project(":core:database"))
  implementation(project(":core:datastore"))
  implementation(libs.androidx.room.ktx)
  implementation(libs.dagger.hilt.android)
  implementation(libs.kotlinx.coroutines.core)
  ksp(libs.dagger.hilt.compiler)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.kotlinx.coroutines.test)
  androidTestImplementation(libs.truth)
}
