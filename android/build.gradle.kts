plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "com.seanproctor"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":demo-common"))
    implementation(compose.material)
    implementation(compose.uiTooling)
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
}

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "com.seanproctor.datatable.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.seanproctor.datatable.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}