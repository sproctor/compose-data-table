pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("android").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["agp.version"] as String)
        id("com.android.library").version(extra["agp.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("com.vanniktech.maven.publish.base").version(extra["vanniktech.version"] as String)
    }
}

rootProject.name = "compose-data-table"

include(":android", ":desktop", ":demo-common", ":data-table", ":data-table-material", ":data-table-material3")
