import org.jetbrains.dependsOnMavenLocalPublication

plugins {
    id("org.jetbrains.conventions.dokka-integration-test")
    id("org.jetbrains.conventions.maven-cli-setup")
}

dependencies {
    implementation(projects.integrationTests)

    implementation(kotlin("test-junit"))
}

tasks.integrationTest {
    dependsOnMavenLocalPublication()

    dependsOn(tasks.installMavenBinary)
    val mvn = mavenCliSetup.mvn
    inputs.file(mvn)

    environment("DOKKA_VERSION", dokkaBuild.dokkaVersion.get())
    doFirst("workaround for https://github.com/gradle/gradle/issues/24267") {
        environment("MVN_BINARY_PATH", mvn.get().asFile.invariantSeparatorsPath)
    }
}
