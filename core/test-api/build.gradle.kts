import org.jetbrains.registerDokkaArtifactPublication

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
    id("org.jetbrains.conventions.maven-publish")
    //id("dev.adamko.dev-publish")
}

dependencies {
    api(projects.core)
    implementation(projects.kotlinAnalysis)
    implementation("junit:junit:4.13.2") // TODO: remove dependency to junit
    implementation(kotlin("reflect"))
}

registerDokkaArtifactPublication {
    artifactId = "dokka-test-api"
}
