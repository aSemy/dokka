import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.0.0"
    kotlin("plugin.serialization") version embeddedKotlinVersion
    `java-test-fixtures`

    `jvm-test-suite`

    idea

//    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.12.1"
}

group = "org.jetbrains.dokka"
version = "2.0.0"

dependencies {
    implementation("org.jetbrains.dokka:dokka-core:1.7.20")

    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    compileOnly("com.android.tools.build:gradle:4.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    testFixturesImplementation(gradleApi())
    testFixturesImplementation(gradleTestKit())

    // note: test dependencies are defined in the testing.suites {} configuration below
}

java {
    withSourcesJar()
}

gradlePlugin {
    plugins.create("dokkaGradlePlugin2") {
        id = "org.jetbrains.dokka2"
        displayName = "Dokka plugin 2"
        description = "Dokka, the Kotlin documentation tool"
        implementationClass = "org.jetbrains.dokka.gradle.DokkaPlugin"
        isAutomatedPublishing = true
    }
}

pluginBundle {
    website = "https://www.kotlinlang.org/"
    vcsUrl = "https://github.com/kotlin/dokka.git"
    tags = listOf("dokka", "kotlin", "kdoc", "android", "documentation")
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        this.freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

val projectTestMavenRepoDir = layout.buildDirectory.dir("test-maven-repo")

publishing {
    repositories {
        maven(projectTestMavenRepoDir) {
            name = "Test"
        }
    }
}


@Suppress("UnstableApiUsage") // jvm test suites are incubating
testing.suites {

    withType<JvmTestSuite>().configureEach {
        useJUnitJupiter()

        dependencies {
            implementation(project.dependencies.gradleTestKit())

            implementation("org.jetbrains.kotlin:kotlin-test:1.7.20")
            //implementation(project.dependencies.kotlin("test")) // helper function doesn't work?

            implementation(project.dependencies.platform("io.kotest:kotest-bom:5.5.4"))
//            implementation("io.kotest:kotest-runner-junit5")
            implementation("io.kotest:kotest-assertions-core")
            implementation("io.kotest:kotest-assertions-json")

            implementation(project.dependencies.testFixtures(project()))
        }
    }

    /** Unit tests suite */
    val test by getting(JvmTestSuite::class)

    /** Functional tests suite */
    val testFunctional by registering(JvmTestSuite::class) {
        testType.set(TestSuiteType.FUNCTIONAL_TEST)

        targets.all {
            testTask.configure {
                shouldRunAfter(test)
                dependsOn(tasks.matching { it.name == "publishAllPublicationsToTestRepository" })

                val funcTestDir = "$buildDir/functional-tests/"
                systemProperties(
                    "testMavenRepoDir" to file(projectTestMavenRepoDir).canonicalPath,
                    "funcTestTempDir" to funcTestDir,
                )

                inputs.dir(projectTestMavenRepoDir)
                outputs.dir(funcTestDir)

                doFirst {
                    File(funcTestDir).deleteRecursively()
                }
            }
        }

//        sources {
//            java {
//                resources {
//                    srcDir(tasks.pluginUnderTestMetadata.map { it.outputDirectory })
//                }
//            }
//        }
    }


    /** Integration tests suite */
    val testIntegration by registering(JvmTestSuite::class) {
        testType.set(TestSuiteType.INTEGRATION_TEST)

        targets.all {
            testTask.configure {
                shouldRunAfter(test, testFunctional)
                dependsOn(tasks.matching { it.name == "publishAllPublicationsToTestRepository" })

                val integrationTestProjectsDir = "$projectDir/integration-testing/projects"
                systemProperties(
                    "testMavenRepoDir" to file(projectTestMavenRepoDir).canonicalPath,
                    "integrationTestProjectsDir" to "$projectDir/integration-testing/projects",
                )

                inputs.dir(projectTestMavenRepoDir)
                outputs.dir(integrationTestProjectsDir)
            }
        }

//        sources {
//            java {
//                resources {
//                    srcDir(tasks.pluginUnderTestMetadata.map { it.outputDirectory })
//                }
//            }
//        }
    }

    tasks.check { dependsOn(testFunctional, testIntegration) }
}


tasks.withType<Test>().configureEach {
    testLogging {
        events = setOf(
            TestLogEvent.STARTED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_OUT,
//         TestLogEvent.STANDARD_ERROR,
        )
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}
