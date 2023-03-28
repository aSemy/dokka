import org.jetbrains.registerDokkaArtifactPublication

plugins {
    id("org.jetbrains.conventions.kotlin-jvm")
    id("org.jetbrains.conventions.maven-publish")
    //id("dev.adamko.dev-publish")
}

dependencies {
    compileOnly(projects.core)

    implementation(kotlin("reflect"))
    implementation(projects.plugins.base)
    implementation(projects.plugins.gfm)
    implementation(projects.plugins.allModulesPage)
    implementation(projects.plugins.templating)

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.testUtils)
    testImplementation(projects.core.testApi)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

registerDokkaArtifactPublication {
    artifactId = "gfm-template-processing-plugin"
}
