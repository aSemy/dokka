@file:Suppress("LocalVariableName")

package org.jetbrains

import org.gradle.api.Project

enum class DokkaPublicationChannel(
    val acceptedDokkaVersionTypes: List<DokkaVersionType>
) {
    SPACE_DOKKA_DEV(listOf(
        DokkaVersionType.RELEASE,
        DokkaVersionType.RC,
        DokkaVersionType.DEV,
        DokkaVersionType.SNAPSHOT
    )),
    MAVEN_CENTRAL(listOf(DokkaVersionType.RELEASE, DokkaVersionType.RC)),
    MAVEN_CENTRAL_SNAPSHOT(listOf(DokkaVersionType.SNAPSHOT)),
    GRADLE_PLUGIN_PORTAL(listOf(DokkaVersionType.RELEASE, DokkaVersionType.RC)),
    MAVEN_PUBLISH_TEST(DokkaVersionType.values().toList()),
    ;

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
        val publicationChannel = this.properties["dokka_publication_channel"]?.toString()
        if (publicationChannel != null) error("replace `dokka_publication_channel` with `dokka_publication_channels`")

        val publicationChannels = this.properties["dokka_publication_channels"]?.toString()
            ?: return emptySet()

        return publicationChannels
            .split("&")
            .map { channel -> DokkaPublicationChannel.fromPropertyString(channel) }
            .toSet()
    }
