package org.jetbrains

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.setProperty
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import javax.inject.Inject

/**
 * Common build properties used to build Dokka subprojects.
 *
 * This is an extension created by the [org.jetbrains.conventions.Base_gradle] convention plugin.
 *
 * Default values are set in the root `gradle.properties`, and can be overridden via
 * [CLI args, system properties, and environment variables](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties)
 */
abstract class DokkaBuildProperties @Inject constructor(
    private val providers: ProviderFactory,
    objects: ObjectFactory,
) {

    val dokkaVersion: Provider<String> =
        dokkaProperty("version") { it }

    val dokkaVersionType: Provider<DokkaVersionType?> =
        dokkaVersion.flatMap {
            providers.provider { DokkaVersionType.find(it) } // https://github.com/gradle/gradle/issues/12388
        }

    /**
     * The main version of Java that should be used to build Dokka source code.
     *
     * Updating the Java target is a breaking change.
     */
    val mainJavaVersion: Provider<JavaLanguageVersion> =
        dokkaProperty("javaToolchain.mainCompiler", JavaLanguageVersion::of)

    /**
     * The version of Java that should be used to run Dokka tests.
     *
     * This value is set in CI/CD environments to make sure that Dokka still works with different
     * versions of Java.
     */
    val testJavaLauncherVersion: Provider<JavaLanguageVersion> =
        dokkaProperty("javaToolchain.testLauncher", JavaLanguageVersion::of)
            .orElse(mainJavaVersion)

    /**
     * The Kotlin language level that Dokka artifacts are compiled to support.
     *
     * Updating the language level is a breaking change.
     */
    val kotlinLanguageLevel: Provider<KotlinVersion> =
        dokkaProperty("kotlinLanguageLevel", KotlinVersion::fromVersion)

    val publicationChannels: Provider<Set<DokkaPublicationChannel>> =
        objects.setProperty<DokkaPublicationChannel>().value(
            dokkaProperty("publicationChannels") { publicationChannels ->
                publicationChannels.split("&")
                    .filter { it.isNotBlank() }
                    .map { channel -> DokkaPublicationChannel.fromPropertyString(channel) }
                    .toSet()
            }
        )

    val signingKeyId: Provider<String> = providers.environmentVariable("SIGN_KEY_ID")
    val signingKey: Provider<String> = providers.environmentVariable("SIGN_KEY")
    val signingKeyPassphrase: Provider<String> = providers.environmentVariable("SIGN_KEY_PASSPHRASE")

    private fun <T : Any> dokkaProperty(name: String, convert: (String) -> T) =
        providers.gradleProperty("org.jetbrains.dokka.$name").map(convert)

    companion object {
        const val EXTENSION_NAME = "dokkaBuild"
    }
}
