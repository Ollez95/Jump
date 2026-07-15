plugins { id("jump.jvm.library") }

dependencies {
  implementation(project(":core:model"))
  implementation(libs.javax.inject)
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.junit)
}
