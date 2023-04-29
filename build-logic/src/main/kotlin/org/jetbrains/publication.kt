package org.jetbrains

import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.DokkaPublicationBuilder.Component.Java
import org.jetbrains.DokkaPublicationBuilder.Component.Shadow

class DokkaPublicationBuilder {
    enum class Component {
        Java, Shadow
    }

    var artifactId: String? = null
    var component: Component = Java
}

fun Project.registerDokkaArtifactPublication(
    configure: DokkaPublicationBuilder.() -> Unit
) {
    val builder = DokkaPublicationBuilder().apply(configure)

    configure<PublishingExtension> {
        publications {
            register<MavenPublication>("maven${builder.component.name}") {
                artifactId = builder.artifactId
                when (builder.component) {
                    Java -> from(components["java"])
                    Shadow -> {
                        extensions.getByType<ShadowExtension>().component(this)
                        artifact(tasks["sourcesJar"])
                    }
                }

                suppressPomMetadataWarningsFor("testFixturesApiElements")
                suppressPomMetadataWarningsFor("testFixturesRuntimeElements")
            }
        }
    }
}
