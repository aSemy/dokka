package org.jetbrains.conventions

import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.jetbrains.conventions.base-java")
    kotlin("jvm")
}

plugins.withType<MavenPublishPlugin>().configureEach {
    // when the subproject is published, apply public-facing compilation options
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=org.jetbrains.dokka.InternalDokkaApi",
                "-Xjsr305=strict",
                "-Xskip-metadata-version-check",
                // need 1.4 support, otherwise there might be problems with Gradle 6 or 7
                // (they require Kotlin 1.4 https://docs.gradle.org/current/userguide/compatibility.html#kotlin)
                "-Xsuppress-version-warnings",
            )
            allWarningsAsErrors.set(true)
            languageVersion.set(dokkaBuild.kotlinLanguageLevel)
            apiVersion.set(dokkaBuild.kotlinLanguageLevel)
        }
    }
}
