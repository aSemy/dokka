@file:Suppress("LocalVariableName")

package org.jetbrains

import org.gradle.api.Project

enum class DokkaPublicationChannel {
    SPACE_DOKKA_DEV,
    MAVEN_CENTRAL,
    MAVEN_CENTRAL_SNAPSHOT,
    GRADLE_PLUGIN_PORTAL;

    val acceptedDokkaVersionTypes: List<DokkaVersionType>
        get() = when (this) {
            MAVEN_CENTRAL -> listOf(DokkaVersionType.RELEASE, DokkaVersionType.RC)
            MAVEN_CENTRAL_SNAPSHOT -> listOf(DokkaVersionType.SNAPSHOT)
            SPACE_DOKKA_DEV -> listOf(
                DokkaVersionType.RELEASE,
                DokkaVersionType.RC,
                DokkaVersionType.DEV,
                DokkaVersionType.SNAPSHOT,
            )

            GRADLE_PLUGIN_PORTAL -> listOf(DokkaVersionType.RELEASE, DokkaVersionType.RC)
        }

    fun isSpaceRepository() = this == SPACE_DOKKA_DEV

    fun isMavenRepository() = this == MAVEN_CENTRAL || this == MAVEN_CENTRAL_SNAPSHOT

    fun isGradlePluginPortal() = this == GRADLE_PLUGIN_PORTAL

    companion object {
        fun fromPropertyString(value: String): DokkaPublicationChannel = when (value) {
            "space-dokka-dev" -> SPACE_DOKKA_DEV
            "maven-central-release" -> MAVEN_CENTRAL
            "maven-central-snapshot" -> MAVEN_CENTRAL_SNAPSHOT
            "gradle-plugin-portal" -> GRADLE_PLUGIN_PORTAL
            else -> throw IllegalArgumentException("Unknown dokka_publication_channel=$value")
        }
    }
}

val Project.publicationChannels: Set<DokkaPublicationChannel>
    get() {
        if ("dokka_publication_channel" in properties.keys) {
            error("dokka_publication_channel is deprecated - use dokka_publication_channels instead")
        }

        val publicationChannels = this.properties["dokka_publication_channels"]?.toString()
            ?: ""

        return publicationChannels
            .split("&")
            .filter { it.isNotBlank() }
            .map { channel -> DokkaPublicationChannel.fromPropertyString(channel) }
            .toSet()
    }
