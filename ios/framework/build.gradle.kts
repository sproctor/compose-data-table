
plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(project(":demo-common"))
      }
    }

    listOf(
      iosX64(),
      iosArm64(),
      iosSimulatorArm64()
    ).forEach { iosTarget ->
      iosTarget.binaries.framework {
        baseName = "ComposeDataTableKt"
        isStatic = true
      }
    }
  }
}
