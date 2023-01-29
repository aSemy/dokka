package org.jetbrains.dokka.gradle.tasks

import kotlinx.serialization.encodeToString
import org.gradle.api.DomainObjectSet
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.jetbrains.dokka.gradle.DokkaPlugin.Companion.jsonMapper
import org.jetbrains.dokka.gradle.dokka_configuration.DokkaConfigurationKxs
import javax.inject.Inject

@CacheableTask
abstract class DokkaConfigurationTask @Inject constructor(
    objects: ObjectFactory,
) : DokkaTask() {

    @get:Input
    abstract val moduleName: Property<String>

    @get:Input
    @get:Optional
    abstract val moduleVersion: Property<String>

    @get:Internal
    // marked as Internal because this task does not use the directory contents, only the location
    val outputDir: DirectoryProperty = objects.directoryProperty()

    /**
     * Because [outputDir] must be [Internal] (so Gradle doesn't check the directory contents),
     * [outputDirPath] is required so Gradle can determine if the task is up-to-date.
     */
    @get:Input
    protected val outputDirPath: Provider<String> = outputDir.map { it.asFile.invariantSeparatorsPath }

    @get:Internal
    // marked as Internal because this task does not use the directory contents, only the location
    val cacheRoot: DirectoryProperty = objects.directoryProperty()

    /**
     * Because [cacheRoot] must be [Internal] (so Gradle doesn't check the directory contents),
     * [cacheRootPath] is required so Gradle can determine if the task is up-to-date.
     */
    @get:Input
    protected val cacheRootPath: Provider<String> = cacheRoot.map { it.asFile.invariantSeparatorsPath }

    @get:Input
    abstract val offlineMode: Property<Boolean>

    @get:Input
    abstract val sourceSets: DomainObjectSet<DokkaConfigurationKxs.DokkaSourceSetKxs>

    @get:InputFiles
    @get:Classpath
    abstract val pluginsClasspath: ConfigurableFileCollection

    @get:Input
    abstract val pluginsConfiguration: DomainObjectSet<DokkaConfigurationKxs.PluginConfigurationKxs>

    /** Dokka Configuration from other subprojects. */
    @get:InputFiles
//    @get:NormalizeLineEndings
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val dokkaModuleDescriptorFiles: ConfigurableFileCollection

    @get:Input
    abstract val failOnWarning: Property<Boolean>

    @get:Input
    abstract val delayTemplateSubstitution: Property<Boolean>

    @get:Input
    abstract val suppressObviousFunctions: Property<Boolean>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val includes: ConfigurableFileCollection

    @get:Input
    abstract val suppressInheritedMembers: Property<Boolean>

    @get:Input
    abstract val finalizeCoroutines: Property<Boolean>

    @get:OutputFile
    abstract val dokkaConfigurationJson: RegularFileProperty

    init {
        description = "Assembles Dokka a configuration file, to be used when executing Dokka"
    }


    @TaskAction
    fun generateConfiguration() {
        val dokkaConfiguration = buildDokkaConfiguration()

        val encodedModuleDesc = jsonMapper.encodeToString(dokkaConfiguration)

        dokkaConfigurationJson.get().asFile.writeText(encodedModuleDesc)
    }

    private fun buildDokkaConfiguration(): DokkaConfigurationKxs {

        val moduleName = moduleName.get()
        val moduleVersion = moduleVersion.orNull?.takeIf { it != "unspecified" }
        val outputDir = outputDir.asFile.get()
        val cacheRoot = cacheRoot.asFile.get()
        val offlineMode = offlineMode.get()
        val sourceSets = sourceSets.toList()
        val pluginsClasspath = pluginsClasspath.files.toList()
        val pluginsConfiguration = pluginsConfiguration.toList()
        val failOnWarning = failOnWarning.get()
        val delayTemplateSubstitution = delayTemplateSubstitution.get()
        val suppressObviousFunctions = suppressObviousFunctions.get()
        val includes = includes.files
        val suppressInheritedMembers = suppressInheritedMembers.get()
        val finalizeCoroutines = finalizeCoroutines.get()

        val dokkaModuleDescriptors = dokkaModuleDescriptors()

        return DokkaConfigurationKxs(
            cacheRoot = cacheRoot,
            delayTemplateSubstitution = delayTemplateSubstitution,
            failOnWarning = failOnWarning,
            finalizeCoroutines = finalizeCoroutines,
            includes = includes,
            moduleName = moduleName,
            moduleVersion = moduleVersion,
            modulesKxs = dokkaModuleDescriptors,
            offlineMode = offlineMode,
            outputDir = outputDir,
            pluginsClasspath = pluginsClasspath,
            pluginsConfiguration = pluginsConfiguration,
            sourceSets = sourceSets,
            suppressInheritedMembers = suppressInheritedMembers,
            suppressObviousFunctions = suppressObviousFunctions,
        )
    }

    private fun dokkaModuleDescriptors(): List<DokkaConfigurationKxs.DokkaModuleDescriptionKxs> {
        val dokkaModuleDescriptorFiles = dokkaModuleDescriptorFiles.files

        return dokkaModuleDescriptorFiles.map { file ->
            val fileContent = file.readText()
            jsonMapper.decodeFromString(
                DokkaConfigurationKxs.DokkaModuleDescriptionKxs.serializer(),
                fileContent,
            )
        }
    }
}
