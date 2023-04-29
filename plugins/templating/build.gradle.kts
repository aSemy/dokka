import org.jetbrains.registerDokkaArtifactPublication

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
    id("org.jetbrains.conventions.maven-publish")
}

registerDokkaArtifactPublication {
    artifactId = "templating-plugin"
}

dependencies {
    compileOnly(projects.core)


    implementation(kotlin("reflect"))
    implementation(projects.plugins.base)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jackson.kotlin)
    constraints {
        implementation(libs.jackson.databind) {
            because("CVE-2022-42003")
        }
    }
    implementation(libs.kotlinx.html)

    implementation(libs.jsoup)

    testImplementation(testFixtures(projects.plugins.base))
    testImplementation(testFixtures(projects.core))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}
