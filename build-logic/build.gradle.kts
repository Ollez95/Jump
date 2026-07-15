plugins { `kotlin-dsl` }

group = "com.example.jump.buildlogic"

dependencies {
  compileOnly("com.android.tools.build:gradle:9.0.1")
  compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
}

gradlePlugin {
  plugins {
    register("androidLibrary") {
      id = "jump.android.library"
      implementationClass = "AndroidLibraryConventionPlugin"
    }
    register("androidCompose") {
      id = "jump.android.compose"
      implementationClass = "AndroidComposeConventionPlugin"
    }
    register("jvmLibrary") {
      id = "jump.jvm.library"
      implementationClass = "JvmLibraryConventionPlugin"
    }
  }
}
