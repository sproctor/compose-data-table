plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library)
}

kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data-table-material3"))
                implementation(compose.material3)
            }
        }
    }

    jvmToolchain(11)
}

android {
    namespace = "com.seanproctor.datatable.demo"
    compileSdk = extra["sdk.compile"].toString().toInt()
    defaultConfig {
        minSdk = extra["sdk.min"].toString().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
