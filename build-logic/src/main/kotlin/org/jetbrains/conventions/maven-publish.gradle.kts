package org.jetbrains.conventions

import org.jetbrains.DokkaPublicationChannel

plugins {
    id("org.jetbrains.conventions.base")
    `maven-publish`
    signing
    id("org.jetbrains.conventions.dokka")
}

val javadocJar by tasks.registering(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles a Javadoc JAR using Dokka HTML"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

publishing {
    repositories {
        // Publish to a project-local Maven directory, for verification. To test, run:
        // ./gradlew publishAllPublicationsToMavenProjectLocalRepository
        // and check $rootDir/build/maven-project-local
//        maven(rootProject.layout.buildDirectory.dir("maven-project-local")) {
//            name = "MavenProjectLocal"
//        }

        maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev") {
            name = DokkaPublicationChannel.SPACE_DOKKA_DEV.name
            credentials {
                username = System.getenv("SPACE_PACKAGES_USER")
                password = System.getenv("SPACE_PACKAGES_SECRET")
            }
        }
    }

    publications.withType<MavenPublication>().configureEach {
//        artifact(javadocJar)

        pom {
            name.convention("Dokka ${project.name}")
            description.convention("Dokka is an API documentation engine for Kotlin and Java, performing the same function as Javadoc for Java")
            url.convention("https://github.com/Kotlin/dokka")

            licenses {
                license {
                    name.convention("The Apache Software License, Version 2.0")
                    url.convention("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.convention("repo")
                }
            }

            developers {
                developer {
                    id.convention("JetBrains")
                    name.convention("JetBrains Team")
                    organization.convention("JetBrains")
                    organizationUrl.convention("https://www.jetbrains.com")
                }
            }

            scm {
                connection.convention("scm:git:git://github.com/Kotlin/dokka.git")
                url.convention("https://github.com/Kotlin/dokka/tree/master")
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {

    val enabledPublicationChannels = dokkaBuild.enabledPublicationChannels
    inputs.property("enabledPublicationChannels", enabledPublicationChannels)

    val isPublicationEnabled = enabledPublicationChannels.map { enabledChannels ->
        enabledChannels.any { enabledChannel ->
            enabledChannel.name == repository.name
        }
    }

    doLast {
        require(this is PublishToMavenRepository)
        val gav = "${publication?.groupId}:${publication?.artifactId}:${publication?.version}"
        logger.lifecycle("Published '${publication?.name}' $gav to repository '${repository?.name}'")
    }
}

// ⬆️ that is trying to be a clever & working version of this ⬇️
//tasks.withType<PublishToMavenRepository>().configureEach {
//    val isPublicationEnabled = provider { repository.name == DokkaPublicationChannel.SPACE_DOKKA_DEV.name }
//    onlyIf("publishing to Space Dokka Dev is enabled") {
//        isPublicationEnabled.get()
//    }
////        if (this.repository.name == SPACE_DOKKA_DEV.name) {
////            this.isEnabled = this.isEnabled && publication.name in publications
////            if (!this.isEnabled) {
////                this.group = "disabled"
////            }
////        }
//}

tasks.register("dokkaPublish") {
    val enabledPublicationChannels = dokkaBuild.enabledPublicationChannels
    inputs.property("enabledPublicationChannels", enabledPublicationChannels)

    dependsOn(
        tasks
            .withType<PublishToMavenRepository>()
            .matching { task ->
                enabledPublicationChannels.get().any { channel ->
                    channel.name == task.repository.name
                }
            }
    )
}

afterEvaluate {
    val signingKeyId = providers.systemProperty("SIGN_KEY_ID")
    val signingKey = providers.systemProperty("SIGN_KEY")
    val signingKeyPassphrase = providers.systemProperty("SIGN_KEY_PASSPHRASE")

    signing {
        if (signingKey.isPresent) {
            if (signingKeyId.orNull?.isNotBlank() == true) {
                useInMemoryPgpKeys(signingKeyId.get(), signingKey.get(), signingKeyPassphrase.get())
            } else {
                useInMemoryPgpKeys(signingKey.get(), signingKeyPassphrase.get())
            }
            sign(publishing.publications)
        }
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf("signatory is present") { signatory?.keyId != null }
}
