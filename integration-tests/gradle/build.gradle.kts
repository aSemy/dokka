import org.jetbrains.dependsOnMavenLocalPublication

plugins {
    id("org.jetbrains.conventions.dokka-integration-test")
}

dependencies {
    implementation(projects.integrationTests)

    implementation(kotlin("test-junit"))
    implementation(gradleTestKit())

    implementation(libs.jsoup)
}

tasks.integrationTest {
    environment("DOKKA_VERSION", dokkaBuild.dokkaVersion.get())
    inputs.dir(file("projects"))
    dependsOnMavenLocalPublication()

    javaLauncher.set(javaToolchains.launcherFor {
        // kotlinx.coroutines requires Java 11+
        languageVersion.set(dokkaBuild.testJavaLauncherVersion.map {
            maxOf(it, JavaLanguageVersion.of(11))
        })
    })
}
