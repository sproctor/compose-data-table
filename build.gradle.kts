plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.vanniktech.base) apply false
}

tasks.wrapper {
    gradleVersion = "8.14.3"
}

allprojects {
    group = "com.seanproctor"
    version = extra["datatable.version"] as String
}
