package org.jetbrains.conventions

import org.gradle.kotlin.dsl.invoke

plugins {
    id("org.jetbrains.dokka")
}


tasks.dokkaHtml {
    val isLocalPublication = provider { gradle.taskGraph.allTasks.any { it is PublishToMavenLocal } }
    onlyIf("not publishing to MavenLocal") { !isLocalPublication.get() }
    outputDirectory.set(layout.buildDirectory.dir("dokka").map { it.asFile })
}
