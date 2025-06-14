package io.orangebuffalo.kotestplaywrightassertions.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class KotlinCodeGenerator {

    private val knownPrefixes = mapOf(
        "is" to "shouldBe",
        "has" to "shouldHave",
        "contains" to "shouldContain",
        "matches" to "shouldMatch"
    )

    fun generateExtensionFunctions(methods: List<AssertionMethod>): String {
        val fileBuilder = FileSpec.builder("io.orangebuffalo.kotestplaywrightassertions", "LocatorAssertions")
            .addImport("com.microsoft.playwright.assertions", "PlaywrightAssertions")
            .addImport("com.microsoft.playwright", "Locator")

        val regularMethods = methods.filter { it.name != "not" }

        // Generate regular assertion methods
        regularMethods.forEach { method ->
            val functionName = transformMethodName(method.name)
            val functionBuilder = FunSpec.builder(functionName)
                .receiver(ClassName("com.microsoft.playwright", "Locator"))

            if (method.javadoc.isNotBlank()) {
                functionBuilder.addKdoc(method.javadoc)
            }

            method.parameters.forEach { param ->
                functionBuilder.addParameter(param.name, typeRefToTypeName(param.type))
            }

            val parameterNames = method.parameters.joinToString(", ") { it.name }
            val assertionCall = if (parameterNames.isNotEmpty()) {
                "PlaywrightAssertions.assertThat(this).${method.name}($parameterNames)"
            } else {
                "PlaywrightAssertions.assertThat(this).${method.name}()"
            }

            functionBuilder.addStatement(assertionCall)
            fileBuilder.addFunction(functionBuilder.build())
        }

        // Generate negated assertion methods
        regularMethods.forEach { method ->
            val functionName = transformMethodNameToNegative(method.name)
            val functionBuilder = FunSpec.builder(functionName)
                .receiver(ClassName("com.microsoft.playwright", "Locator"))

            if (method.javadoc.isNotBlank()) {
                val negatedJavadoc = "Negated version of ${method.name}.\n\n${method.javadoc}"
                functionBuilder.addKdoc(negatedJavadoc)
            }

            method.parameters.forEach { param ->
                functionBuilder.addParameter(param.name, typeRefToTypeName(param.type))
            }

            val parameterNames = method.parameters.joinToString(", ") { it.name }
            val assertionCall = if (parameterNames.isNotEmpty()) {
                "PlaywrightAssertions.assertThat(this).not().${method.name}($parameterNames)"
            } else {
                "PlaywrightAssertions.assertThat(this).not().${method.name}()"
            }

            functionBuilder.addStatement(assertionCall)
            fileBuilder.addFunction(functionBuilder.build())
        }

        return fileBuilder.build().toString()
    }

    private fun transformMethodName(methodName: String): String {
        for ((prefix, replacement) in knownPrefixes) {
            if (methodName.startsWith(prefix) && methodName.length > prefix.length) {
                val suffix = methodName.substring(prefix.length)
                return replacement + suffix
            }
        }
        throw IllegalArgumentException("Unknown method prefix for method: $methodName")
    }

    private fun transformMethodNameToNegative(methodName: String): String {
        for ((prefix, replacement) in knownPrefixes) {
            if (methodName.startsWith(prefix) && methodName.length > prefix.length) {
                val suffix = methodName.substring(prefix.length)
                return replacement.replace("should", "shouldNot") + suffix
            }
        }
        throw IllegalArgumentException("Unknown method prefix for method: $methodName")
    }

    private fun typeRefToTypeName(typeRef: TypeRef): TypeName = when (typeRef) {
        is TypeRef.Simple -> when (typeRef.name) {
            "String" -> String::class.asTypeName()
            "Boolean" -> Boolean::class.asTypeName()
            "Int" -> Int::class.asTypeName()
            "Long" -> Long::class.asTypeName()
            "Double" -> Double::class.asTypeName()
            "Float" -> Float::class.asTypeName()
            "Byte" -> Byte::class.asTypeName()
            "Char" -> Char::class.asTypeName()
            "Short" -> Short::class.asTypeName()
            "Unit" -> Unit::class.asTypeName()
            "Any" -> Any::class.asTypeName()
            else -> {
                if (typeRef.name.contains(".")) {
                    val parts = typeRef.name.split(".")
                    ClassName(parts.dropLast(1).joinToString("."), parts.last())
                } else {
                    ClassName.bestGuess(typeRef.name)
                }
            }
        }
        is TypeRef.Array -> Array::class.asClassName().parameterizedBy(typeRefToTypeName(typeRef.elementType))
        is TypeRef.Generic -> {
            val className = when (typeRef.rawType) {
                "kotlin.collections.List" -> ClassName("kotlin.collections", "List")
                "kotlin.collections.Map" -> ClassName("kotlin.collections", "Map")
                "kotlin.collections.Set" -> ClassName("kotlin.collections", "Set")
                "kotlin.collections.Collection" -> ClassName("kotlin.collections", "Collection")
                "kotlin.collections.Iterable" -> ClassName("kotlin.collections", "Iterable")
                else -> if (typeRef.rawType.contains(".")) {
                    val parts = typeRef.rawType.split(".")
                    ClassName(parts.dropLast(1).joinToString("."), parts.last())
                } else {
                    ClassName.bestGuess(typeRef.rawType)
                }
            }
            className.parameterizedBy(typeRef.typeArguments.map { typeRefToTypeName(it) })
        }
    }
}
