plugins {
    alias(libs.plugins.datatable.library)
}

kotlin {

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data-table-material3"))
                implementation(compose.material3)
            }
        }

        val skikoMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(skikoMain)
        }

        val jsMain by getting {
            dependsOn(skikoMain)
        }
    }
}

android {
    namespace = "com.seanproctor.datatable.demo"
}
