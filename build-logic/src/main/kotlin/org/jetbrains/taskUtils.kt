package org.jetbrains

import org.gradle.api.Task
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.kotlin.dsl.withType

fun Task.dependsOnMavenLocalPublication() {
    project.rootProject.allprojects.forEach { otherProject ->
        dependsOn(
            otherProject.tasks.withType<PublishToMavenLocal>()
        )
    }
}
