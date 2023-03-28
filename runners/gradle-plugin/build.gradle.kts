//import org.jetbrains.configureSonatypePublicationIfNecessary
//import org.jetbrains.configureSpacePublicationIfNecessary
//import org.jetbrains.createDokkaPublishTaskIfNecessary

plugins {
    `kotlin-dsl`
    id("org.jetbrains.conventions.maven-publish")
    //id("dev.adamko.dev-publish")
    id("org.jetbrains.conventions.base-java")
    id("com.gradle.plugin-publish")
}

dependencies {
    api(projects.core)

    compileOnly(libs.gradlePlugin.kotlin)
    compileOnly(libs.gradlePlugin.android)

    testImplementation(libs.gradlePlugin.kotlin)
    testImplementation(libs.gradlePlugin.android)
}

// Gradle will put its own version of the stdlib in the classpath, so not pull our own we will end up with
// warnings like 'Runtime JAR files in the classpath should have the same version'
configurations.api.configure {
    /**
     * These dependencies will be provided by Gradle, and we should prevent version conflict
     * Code taken from the Kotlin Gradle plugin:
     * https://github.com/JetBrains/kotlin/blob/70e15b281cb43379068facb82b8e4bcb897a3c4f/buildSrc/src/main/kotlin/GradleCommon.kt#L72
     */
    dependencies
        .withType<ModuleDependency>()
        .configureEach {
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-script-runtime")
        }
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    isAutomatedPublishing = true

    website.set("https://www.kotlinlang.org/")
    vcsUrl.set("https://github.com/kotlin/dokka.git")

    plugins.create("dokkaGradlePlugin") {
        id = "org.jetbrains.dokka"
        displayName = "Dokka plugin"
        description = "Dokka, the Kotlin documentation tool"
        implementationClass = "org.jetbrains.dokka.gradle.DokkaPlugin"
        tags.addAll("dokka", "kotlin", "kdoc", "android", "documentation")
    }
}

//pluginBundle {
//    website = ""
//    vcsUrl = ""
//    tags = listOf
//
//    mavenCoordinates {
//        groupId = "org.jetbrains.dokka"
//        artifactId = "dokka-gradle-plugin"
//    }
//}

publishing {
    publications {
//        register<MavenPublication>("dokkaGradlePluginForIntegrationTests") {
//            artifactId = "dokka-gradle-plugin"
//            from(components["java"])
//            version = "for-integration-tests-SNAPSHOT"
//        }

////        afterEvaluate {
//        withType<MavenPublication>()
//            .matching { it.name == "mavenJava" }
//            .configureEach {
//                artifactId = "dokka-gradle-plugin"
//            }
//
        register<MavenPublication>("pluginMaven") {
////            configurePom("Dokka ${project.name}")
            artifactId = "dokka-gradle-plugin"
        }
////        }

        withType<MavenPublication>()
//            .matching { it.name == "dokkaGradlePluginPluginMarkerMaven" }
            .configureEach {
//                configurePom("Dokka plugin")
                pom {
                    name.set("Dokka Gradle Plugin")
                }
            }
    }
}

//afterEvaluate {
//    error(
//"""
//${publishing.publications.names.joinToString("\n")}
//${
//tasks.withType<PublishToMavenRepository>()
//.joinToString("\n") { "task ${it.name} publication ${it.publication?.name}" }
//}
//
//""".trimIndent()
//    )
//}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

//tasks.withType<PublishToMavenRepository>().configureEach {
//    onlyIf { publication != publishing.publications["dokkaGradlePluginForIntegrationTests"] }
//}

afterEvaluate { // Workaround for an interesting design choice https://github.com/gradle/gradle/blob/c4f935f77377f1783f70ec05381c8182b3ade3ea/subprojects/plugin-development/src/main/java/org/gradle/plugin/devel/plugins/MavenPluginPublishPlugin.java#L49
//    configureSpacePublicationIfNecessary("pluginMaven", "dokkaGradlePluginPluginMarkerMaven")
//    configureSonatypePublicationIfNecessary("pluginMaven", "dokkaGradlePluginPluginMarkerMaven")
//    configureSpacePublicationIfNecessary()
//    configureSonatypePublicationIfNecessary()
//    createDokkaPublishTaskIfNecessary()
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
