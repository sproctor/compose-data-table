plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.vanniktech.base) apply false
}

tasks.wrapper {
    gradleVersion = "8.12.1"
}

allprojects {
    group = "com.seanproctor"
    version = extra["datatable.version"] as String
}
