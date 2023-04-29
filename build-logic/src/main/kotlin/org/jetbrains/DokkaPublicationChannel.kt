@file:Suppress("LocalVariableName")

package org.jetbrains

import org.gradle.api.artifacts.repositories.MavenArtifactRepository

enum class DokkaPublicationChannel {
    SPACE_DOKKA_DEV,
    MAVEN_CENTRAL,
    MAVEN_CENTRAL_SNAPSHOT,
    GRADLE_PLUGIN_PORTAL,
    MAVEN_PROJECT_LOCAL,
    ;

    val acceptedDokkaVersionTypes: List<DokkaVersionType>
        get() = when (this) {
            MAVEN_CENTRAL -> listOf(DokkaVersionType.RELEASE, DokkaVersionType.RC)
            MAVEN_CENTRAL_SNAPSHOT -> listOf(DokkaVersionType.SNAPSHOT)
            GRADLE_PLUGIN_PORTAL -> listOf(DokkaVersionType.RELEASE, DokkaVersionType.RC)
            SPACE_DOKKA_DEV,
            MAVEN_PROJECT_LOCAL -> DokkaVersionType.values().toList()
        }

    val prettyName: String =
        name
            .split("_")
            .joinToString("") { it.toLowerCase().capitalize() }

    fun isSpaceRepository() = this == SPACE_DOKKA_DEV

    fun isMavenRepository() = this == MAVEN_CENTRAL || this == MAVEN_CENTRAL_SNAPSHOT

    fun isGradlePluginPortal() = this == GRADLE_PLUGIN_PORTAL

    companion object {
        fun fromPropertyString(value: String): DokkaPublicationChannel = when (value) {
            "space-dokka-dev" -> SPACE_DOKKA_DEV
            "maven-central-release" -> MAVEN_CENTRAL
            "maven-central-snapshot" -> MAVEN_CENTRAL_SNAPSHOT
            "gradle-plugin-portal" -> GRADLE_PLUGIN_PORTAL
            else -> throw IllegalArgumentException("Unknown dokka_publication_channel '$value'")
        }

        fun fromRepository(value: MavenArtifactRepository?): DokkaPublicationChannel? {
            if (value == null) return null

            val urlString = value.url.toString()

            // Maven Central repo is added automatically by io.github.gradle-nexus.publish-plugin,
            // so we have to determine the channel based on the URL
            return if ("https://oss.sonatype.org" in urlString) {
                when {
                    "snapshot" in urlString -> MAVEN_CENTRAL_SNAPSHOT
                    else -> MAVEN_CENTRAL
                }
            } else {
                // the other repositories are added manually, or have distinct names, and can be found by name
                // (assuming that prettyName was used to name the repo)
                values().find { it.prettyName == value.name }
            }
        }
    }
}
