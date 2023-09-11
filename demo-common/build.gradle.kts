import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    jvm("desktop") {
        jvmToolchain(11)
    }
    js(IR) {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data-table-material"))
                implementation(compose.material)
            }
        }
    }
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
