package org.jetbrains

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.setProperty
import org.jetbrains.DokkaPublicationChannel.*
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
    private val objects: ObjectFactory,
) {

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

    val enabledPublicationChannels: SetProperty<DokkaPublicationChannel> =
        objects.setProperty<DokkaPublicationChannel>()
            .convention(
                setOf(
                    SPACE_DOKKA_DEV,
                    MAVEN_CENTRAL,
                    MAVEN_CENTRAL_SNAPSHOT,
                    MAVEN_PUBLISH_TEST,
                )
            )


    private fun <T : Any> dokkaProperty(name: String, convert: (String) -> T) =
        providers.gradleProperty("org.jetbrains.dokka.$name").map(convert)

    companion object {
        const val EXTENSION_NAME = "dokkaBuild"
    }
}
