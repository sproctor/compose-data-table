plugins {
    alias(libs.plugins.datatable.library)
    alias(libs.plugins.datatable.publish)
}

kotlin {
    sourceSets {
        commonMain  {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }
    }
}
