pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.6"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "compose-data-table"

include(":datatable")
include(":datatable-material3")

// Demo apps
include(":demoApp")

refreshVersions {
    file("build/tmp/refreshVersions").mkdirs()
    versionsPropertiesFile = file("build/tmp/refreshVersions/versions.properties")
    rejectVersionIf {
        candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
    }
}

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    Compose Data Table requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}
