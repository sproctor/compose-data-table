plugins {
    alias(libs.plugins.datatable.android.application)
}

dependencies {
    implementation(project(":demo-common"))
    implementation(compose.material)
    implementation(compose.uiTooling)
    implementation(libs.activity.compose)
}

android {
    namespace = "com.seanproctor.datatable.android"
    defaultConfig {
        applicationId = "com.seanproctor.datatable.android"
        versionCode = 1
        versionName = "1.0"
    }
}

kotlin {
    jvmToolchain(11)
}
