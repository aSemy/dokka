/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
}

dependencies {
    compileOnly(projects.core)
    compileOnly(projects.analysisKotlinApi)

    implementation(projects.analysisKotlinDescriptorsCompiler)

    // TODO [beresnev] get rid of it
    compileOnly(libs.kotlinx.coroutines.core)
}
