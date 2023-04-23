package org.jetbrains

import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.kotlin.dsl.*
import org.jetbrains.DokkaPublicationChannel.SPACE_DOKKA_DEV
import java.net.URI

class DokkaPublicationBuilder {
    enum class Component {
        Java, Shadow
    }

    var artifactId: String? = null
    var component: Component = Component.Java
}


fun Project.registerDokkaArtifactPublication(
    publicationName: String,
    configure: DokkaPublicationBuilder.() -> Unit
) {
    configure<PublishingExtension> {
        publications {
            register<MavenPublication>(publicationName) {
                val builder = DokkaPublicationBuilder().apply(configure)
                artifactId = builder.artifactId
                when (builder.component) {
                    DokkaPublicationBuilder.Component.Java -> from(components["java"])
                    DokkaPublicationBuilder.Component.Shadow -> run {
                        extensions.getByType<ShadowExtension>().component(this)
                        artifact(tasks["sourcesJar"])
                    }
                }
            }
        }
    }

    createDokkaPublishTaskIfNecessary()
}


fun Project.createDokkaPublishTaskIfNecessary() {
    tasks.maybeCreate("dokkaPublish").run {
        if (publicationChannels.any { it.isSpaceRepository() }) {
            dependsOn(tasks.named("publish"))
        }

        if (publicationChannels.any { it.isMavenRepository() }) {
            dependsOn(tasks.named("publishToSonatype"))
        }

        if (publicationChannels.any { it.isGradlePluginPortal() }) {
            dependsOn(tasks.named("publishPlugins"))
        }
    }
}
