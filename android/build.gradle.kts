plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "com.seanproctor"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":datatable"))
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
}

kotlin {
    jvmToolchain(11)
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.seanproctor.datatable.demo"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}