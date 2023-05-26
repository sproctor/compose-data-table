plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.vanniktech.maven.publish.base")
}

group = "com.seanproctor"
version = extra["datatable.version"] as String

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        jvmToolchain(11)
    }
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":data-table"))
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
    }
}

android {
    namespace = "com.seanproctor.datatable"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
    configure(
        com.vanniktech.maven.publish.KotlinMultiplatform(javadocJar = com.vanniktech.maven.publish.JavadocJar.Empty())
    )
}
