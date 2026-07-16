pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("build-logic")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "jump"
include(":app")
include(":core:model")
include(":core:domain")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":core:designsystem")
include(":core:workout")
include(":core:camera")
include(":feature:onboarding")
include(":feature:home")
include(":feature:workout")
include(":feature:workoutsetup")
include(":feature:history")
include(":feature:progress")
include(":feature:settings")
