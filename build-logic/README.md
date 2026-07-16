# `build-logic`

Local Gradle convention plugins shared by Jump modules. Keeping build defaults here prevents SDK, Java, and Compose configuration from drifting across the project.

## Plugins

- `jump.android.library` applies the Android library plugin, SDK 37.1, minimum SDK 24, Java 17, packaging exclusions, and lean build-feature defaults.
- `jump.android.compose` enables the Kotlin Compose plugin and Compose build feature for an Android library.
- `jump.jvm.library` applies Kotlin/JVM and selects the Java 17 toolchain.

The included build is registered from the root `settings.gradle.kts`. Plugin implementation dependencies intentionally align with the root version catalog.

## Use

```kotlin
plugins {
  id("jump.android.library")
  id("jump.android.compose")
}
```

Apply only the conventions a module needs. Android application configuration remains explicit in `:app`.

## Verification

```bash
./gradlew help
```

Changes here can affect every module, so follow with `./gradlew assembleDebug`.

[Back to the project README](../README.md)

