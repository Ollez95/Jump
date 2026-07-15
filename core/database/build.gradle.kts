plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.core.database" }

dependencies {
  implementation(project(":core:model"))
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  implementation(libs.dagger.hilt.android)
  ksp(libs.androidx.room.compiler)
  ksp(libs.dagger.hilt.compiler)
  testImplementation(libs.androidx.room.testing)
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }
