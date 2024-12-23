import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

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
    gradleVersion = "8.9"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    // Credentials must be added to ~/.gradle/gradle.properties per
    // https://vanniktech.github.io/gradle-maven-publish-plugin/central/#secrets
    plugins.withId("com.vanniktech.maven.publish.base") {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "testMaven"
                    url = layout.buildDirectory.file("testMaven").get().asFile.toURI()
                }
            }
        }
        configure<MavenPublishBaseExtension> {
            publishToMavenCentral(SonatypeHost.S01)
            signAllPublications()
            pom {
                description.set("A Material Design data table implementation for Compose Multiplatform.")
                name.set(project.name)
                url.set("https://github.com/sproctor/compose-data-table")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("sproctor")
                        name.set("Sean Proctor")
                        email.set("sproctor@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/sproctor/compose-data-table/tree/main")
                }
            }
        }
    }
}
