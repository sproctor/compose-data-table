plugins {
    alias(libs.plugins.datatable.library)
    alias(libs.plugins.datatable.publish)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":datatable"))
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
            }
        }
    }
}
