plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(project(":demo-common"))
    implementation(compose.uiTooling)
    implementation(libs.activity.compose)
}

android {
    namespace = "com.seanproctor.datatable.android"
    compileSdk = 36
    defaultConfig {
        minSdk = 21
        targetSdk = 36
        applicationId = "com.seanproctor.datatable.android"
        versionCode = 1
        versionName = "1.0"
    }
}

kotlin {
    jvmToolchain(11)
}
