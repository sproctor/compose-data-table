@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

group = "com.seanproctor"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget()
    jvm()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "DemoApp"
            isStatic = true
        }
    }
    wasmJs {
        outputModuleName = "demo"
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.activity.compose)
                implementation(compose.uiTooling)
            }
        }
        commonMain {
            dependencies {
                implementation(project(":datatable-material3"))
                implementation(compose.material3)
                implementation(libs.fastscroller.m3)
            }
        }
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }

    jvmToolchain(11)
}

android {
    namespace = "com.seanproctor.datatable.android"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        targetSdk = 36
        applicationId = "com.seanproctor.datatable.android"
        versionCode = 1
        versionName = "1.0"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
