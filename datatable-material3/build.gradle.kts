plugins {
    alias(libs.plugins.datatable.library)
    alias(libs.plugins.datatable.publish)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":datatable"))
                implementation(libs.compose.material3)
                implementation(libs.compose.components.resources)
            }
        }
    }
}
