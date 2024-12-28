import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.JavadocJar.Empty

// Credentials must be added to ~/.gradle/gradle.properties per
// https://vanniktech.github.io/gradle-maven-publish-plugin/central/#secrets
class PublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.vanniktech.maven.publish.base")
            }

            with(extensions) {
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
                    configure(KotlinMultiplatform(javadocJar = Empty()))
                }
            }
        }
    }
}
