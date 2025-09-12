package com.seanproctor.datatable

import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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
        androidLibrary {
            minSdk = 21
            compileSdk = 36
            namespace = "com.seanproctor." + project.name.replace("-", ".")
        }
        jvm()
        js {
            browser()
            useEsModules()
        }
        wasmJs {
            browser()
        }
        iosArm64()
        iosX64()
        iosSimulatorArm64()
    }
}
