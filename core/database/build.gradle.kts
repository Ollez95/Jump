plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android {
  namespace = "com.example.jump.core.database"
  defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
  implementation(project(":core:model"))
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  implementation(libs.dagger.hilt.android)
  ksp(libs.androidx.room.compiler)
  ksp(libs.dagger.hilt.compiler)
  testImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.truth)
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }
