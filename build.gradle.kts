@Suppress("DSL_SCOPE_VIOLATION") // fixed in Gradle 8.1 https://github.com/gradle/gradle/pull/23639
plugins {
    id("org.jetbrains.conventions.base")
    id("org.jetbrains.conventions.dokka")

    alias(libs.plugins.gradlePublish)
    alias(libs.plugins.nexusPublish)
}

group = "org.jetbrains.dokka"
version = dokkaBuild.dokkaVersion.get()


logger.lifecycle("Publication version: ${project.version}")

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_USER"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}

val dokkaPublish by tasks.registering {
    group = PublishingPlugin.PUBLISH_TASK_GROUP

    if (dokkaBuild.publicationChannels.get().any { it.isMavenRepository() }) {
        finalizedBy(tasks.named("closeAndReleaseSonatypeStagingRepository"))
    }
}
