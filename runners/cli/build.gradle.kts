import org.jetbrains.DokkaPublicationBuilder.Component.Shadow
import org.jetbrains.registerDokkaArtifactPublication

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
    id("org.jetbrains.conventions.maven-publish")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlinx.cli)

    testImplementation(kotlin("test-junit"))
}

tasks {
    shadowJar {
        archiveBaseName.set("dokka-cli")
        archiveVersion.set(dokkaBuild.dokkaVersion)
        archiveClassifier.set("")
        manifest {
            attributes("Main-Class" to "org.jetbrains.dokka.MainKt")
        }
    }
}

registerDokkaArtifactPublication {
    artifactId = "dokka-cli"
    component = Shadow
}
