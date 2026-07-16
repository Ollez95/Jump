plugins {
  id("jump.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android { namespace = "com.example.jump.core.data" }

dependencies {
  implementation(project(":core:model"))
  implementation(project(":core:domain"))
  implementation(project(":core:database"))
  implementation(project(":core:datastore"))
  implementation(libs.androidx.room.ktx)
  implementation(libs.dagger.hilt.android)
  implementation(libs.kotlinx.coroutines.core)
  ksp(libs.dagger.hilt.compiler)
}
