plugins {
    alias(libs.plugins.datatable.library)
    alias(libs.plugins.datatable.publish)
}

kotlin {
//    androidLibrary {
//        namespace = "com.seanproctor.datatable"
//    }
    sourceSets {
        commonMain  {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }
    }
}
