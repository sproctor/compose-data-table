plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

group = "com.seanproctor"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":demo-common"))
    implementation(compose.material)
    implementation(compose.uiTooling)
    implementation(libs.activity.compose)
}

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "com.seanproctor.datatable.android"
    compileSdk = extra["sdk.compile"].toString().toInt()
    defaultConfig {
        applicationId = "com.seanproctor.datatable.android"
        minSdk = extra["sdk.min"].toString().toInt()
        targetSdk = extra["sdk.compile"].toString().toInt()
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}