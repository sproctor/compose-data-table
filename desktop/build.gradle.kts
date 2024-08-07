plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

group = "com.seanproctor"
version = "1.0-SNAPSHOT"


kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":demo-common"))
                implementation(compose.desktop.currentOs)
            }
        }
    }

    jvmToolchain(11)
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
