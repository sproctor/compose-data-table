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
                implementation(libs.fastscroller.m3)
            }
        }
    }
}

android {
    namespace = "com.seanproctor.datatable.demo"
}
