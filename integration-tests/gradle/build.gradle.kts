//import org.jetbrains.dependsOnMavenLocalPublication

plugins {
    id("org.jetbrains.conventions.dokka-integration-test")
    //id("dev.adamko.dev-publish")
}

dependencies {
    implementation(projects.integrationTests)

    implementation(kotlin("test-junit"))
    implementation(gradleTestKit())

    implementation(libs.jsoup)

//    devPublication(projects.core)
//    devPublication(projects.plugins.base)
//    devPublication(projects.plugins.javadoc)
//    devPublication(projects.plugins.kotlinAsJava)
//    devPublication(projects.plugins.gfm)
//    devPublication(projects.plugins.jekyll)
//    devPublication(projects.kotlinAnalysis)
//    devPublication(projects.kotlinAnalysis.intellijDependency)
//    devPublication(projects.kotlinAnalysis.compilerDependency)
//    devPublication(projects.runners.gradlePlugin)
}

tasks.integrationTest {
    val dokka_version: String by project
    environment("DOKKA_VERSION", dokka_version)//.replace("-SNAPSHOT", "-TEST"))
    inputs.dir(file("projects"))
    //dependsOnMavenLocalPublication()

    javaLauncher.set(javaToolchains.launcherFor {
        // kotlinx.coroutines requires Java 11+
        languageVersion.set(dokkaBuild.testJavaLauncherVersion.map {
            maxOf(it, JavaLanguageVersion.of(11))
        })
    })

//    dependsOn(tasks.updateDevRepo)
////    dependsOn(configurations.mavenPublishTest)
//
//    environment(
//        "MAVEN_PUBLISH_TEST_REPO" to devPublish.devMavenRepo.asFile.get().invariantSeparatorsPath
//    )
}
