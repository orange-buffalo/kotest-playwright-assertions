package io.orangebuffalo.kotestplaywrightassertions.generator

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateLocatorAssertionsTask : DefaultTask() {

    @get:Input
    abstract val targetPlaywrightVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val version = targetPlaywrightVersion.get()
        val outputDir = outputDirectory.get().asFile

        val versionResolver = PlaywrightVersionResolver()
        val resolvedVersion = versionResolver.resolveVersion(version)

        val fetcher = SourceCodeFetcher()
        val sourceCode = fetcher.fetchLocatorAssertions(resolvedVersion)

        val parser = JavaSourceParser()
        val methods = parser.parseAssertionMethods(sourceCode)

        val transformer = JavaDocTransformer()
        val transformedMethods = methods
        // TODO #4 improve javdocs conversion
//        val transformedMethods = methods.map { method ->
//            method.copy(javadoc = transformer.transformJavaDoc(method.javadoc))
//        }

        val generator = KotlinCodeGenerator()
        val kotlinCode = generator.generateExtensionFunctions(transformedMethods)

        val packageDir = File(outputDir, "io/orangebuffalo/kotestplaywrightassertions")
        packageDir.mkdirs()

        val outputFile = File(packageDir, "LocatorAssertions.kt")
        outputFile.writeText(kotlinCode)

        logger.info("Generated ${transformedMethods.size} extension functions in ${outputFile.absolutePath}")
    }
}
