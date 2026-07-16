plugins { `kotlin-dsl` }

group = "com.example.jump.buildlogic"

dependencies {
  compileOnly("com.android.tools.build:gradle:9.3.0")
  compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
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
