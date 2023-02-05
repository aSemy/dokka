@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.dokka.gradle

import org.jetbrains.dokka.gradle.dokka_configuration.DokkaSourceSetGradleBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

@Deprecated("dokka2")
typealias DokkaTask = org.jetbrains.dokka.gradle.tasks.DokkaConfigurationTask


/**
 * Extension allowing configuration of Dokka source sets via Kotlin Gradle plugin source sets.
 */
@Deprecated("dokka2")
fun DokkaSourceSetGradleBuilder.kotlinSourceSet(kotlinSourceSet: KotlinSourceSet) {
    // TODO write adapter to new DSL
}
