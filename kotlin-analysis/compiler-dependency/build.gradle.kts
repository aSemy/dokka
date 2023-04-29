import org.jetbrains.DokkaPublicationBuilder.Component.Shadow
import org.jetbrains.registerDokkaArtifactPublication

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
    id("org.jetbrains.conventions.maven-publish")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(libs.kotlin.compiler)
}

tasks {
    shadowJar {
        archiveBaseName.set("dokka-kotlin-analysis-compiler")
        archiveVersion.set(dokkaBuild.dokkaVersion)
        archiveClassifier.set("")
        exclude("com/intellij/")
    }
}

registerDokkaArtifactPublication {
    artifactId = "kotlin-analysis-compiler"
    component = Shadow
}

binaryCompatibilityValidator {
    enabled.set(false)
}
