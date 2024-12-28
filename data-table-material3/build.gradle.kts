plugins {
    alias(libs.plugins.datatable.library)
    alias(libs.plugins.datatable.publish)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":data-table"))
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
            }
        }
    }
}

android {
    namespace = "com.seanproctor.datatable.material3"
}
