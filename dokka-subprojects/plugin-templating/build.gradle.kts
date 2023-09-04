/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.registerDokkaArtifactPublication

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
    id("org.jetbrains.conventions.maven-publish")
}

registerDokkaArtifactPublication("templating-plugin") {
    artifactId = "templating-plugin"
}

dependencies {
    compileOnly(projects.core)

    api(libs.jsoup)

    implementation(projects.pluginBase)

    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiterParams)

    testImplementation(projects.pluginBaseTestUtils)
    testImplementation(projects.coreTestApi)
    testImplementation(libs.kotlinx.html)
}
