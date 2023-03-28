import org.jetbrains.dokkaVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.registerDokkaArtifactPublication

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
    id("org.jetbrains.conventions.maven-publish")
    //id("dev.adamko.dev-publish")
}

dependencies {
    api(libs.jetbrainsMarkdown)
    implementation(kotlin("reflect"))

    implementation(libs.jsoup)

    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.xml)
    constraints {
        implementation(libs.jackson.databind) {
            because("CVE-2022-42003")
        }
    }

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.core.testApi)
    testImplementation(kotlin("test-junit"))
}

tasks {
    processResources {
        inputs.property("dokkaVersion", dokkaVersion)
        eachFile {
            if (name == "dokka-version.properties") {
                filter { line ->
                    line.replace("<dokka-version>", dokkaVersion)
                }
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=org.jetbrains.dokka.InternalDokkaApi")
    }
}

registerDokkaArtifactPublication {
    artifactId = "dokka-core"
}
