pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "compose-data-table"

include(":android", ":desktop", ":demo-common", ":data-table", ":data-table-material", ":data-table-material3")
