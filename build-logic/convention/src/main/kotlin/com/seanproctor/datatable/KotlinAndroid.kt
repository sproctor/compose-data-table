package com.seanproctor.datatable

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {

        compileSdk = 35
        defaultConfig {
            minSdk = 21
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }
}

/**
 * Configure base Kotlin options for all targets
 */
@OptIn(ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatform(
    extension: KotlinMultiplatformExtension
) {
    extension.apply {
        jvmToolchain(11)

        // targets
        androidTarget {
            publishLibraryVariants("release")
        }
        jvm()
        js {
            browser()
            useEsModules()
        }
        wasmJs {
            browser()
        }
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }
}
