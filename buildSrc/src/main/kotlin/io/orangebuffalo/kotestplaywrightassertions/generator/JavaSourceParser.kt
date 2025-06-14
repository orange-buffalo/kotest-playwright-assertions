package io.orangebuffalo.kotestplaywrightassertions.generator

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.TypeDeclaration

sealed class TypeRef {
    data class Simple(val name: String) : TypeRef()
    data class Generic(val rawType: String, val typeArguments: List<TypeRef>) : TypeRef()
    data class Array(val elementType: TypeRef) : TypeRef()
}

data class AssertionMethod(
    val name: String,
    val parameters: List<MethodParameter>,
    val javadoc: String
)

data class MethodParameter(
    val name: String,
    val type: TypeRef
)

class JavaSourceParser {

    private lateinit var compilationUnit: CompilationUnit
    private lateinit var explicitImports: Map<String, String>
    private lateinit var starImports: List<String>
    private lateinit var nestedClasses: Map<String, String>

    fun parseAssertionMethods(sourceCode: String): List<AssertionMethod> {
        val parser = JavaParser()
        compilationUnit = parser.parse(sourceCode).result.orElseThrow()

        explicitImports = buildExplicitImports()
        starImports = buildStarImports()
        nestedClasses = buildNestedClassesMap()

        val locatorAssertionsClass = compilationUnit.types
            .find { it.nameAsString == "LocatorAssertions" }
            ?: throw IllegalStateException("LocatorAssertions class not found")

        return locatorAssertionsClass.methods
            .filter { it.isPublic && !it.isStatic }
            .filter { it.nameAsString != "equals" && it.nameAsString != "hashCode" && it.nameAsString != "toString" }
            .map { method ->
                AssertionMethod(
                    name = method.nameAsString,
                    parameters = method.parameters.map { param ->
                        MethodParameter(
                            name = param.nameAsString,
                            type = parseTypeRef(param.typeAsString)
                        )
                    },
                    javadoc = extractJavadoc(method)
                )
            }
    }

    private fun buildExplicitImports(): Map<String, String> {
        return compilationUnit.imports
            .filter { !it.isAsterisk }
            .associate { import ->
                val fullName = import.nameAsString
                val simpleName = fullName.substringAfterLast('.')
                simpleName to fullName
            }
    }

    private fun buildStarImports(): List<String> {
        return compilationUnit.imports
            .filter { it.isAsterisk }
            .map { it.nameAsString }
    }

    private fun buildNestedClassesMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        fun collectNestedClasses(typeDeclaration: TypeDeclaration<*>, parentName: String) {
            typeDeclaration.members.forEach { member ->
                if (member is ClassOrInterfaceDeclaration) {
                    val nestedName = member.nameAsString
                    val fullName = "$parentName.$nestedName"
                    result[nestedName] = fullName
                    collectNestedClasses(member, fullName)
                }
            }
        }

        compilationUnit.types.forEach { type ->
            val packageName = compilationUnit.packageDeclaration.map { it.nameAsString }.orElse("")
            val className = type.nameAsString
            val fullClassName = if (packageName.isNotEmpty()) "$packageName.$className" else className
            collectNestedClasses(type, fullClassName)
        }

        return result
    }

    private fun parseTypeRef(javaType: String): TypeRef {
        // Handle array types recursively
        if (javaType.endsWith("[]")) {
            val elementType = javaType.removeSuffix("[]")
            return TypeRef.Array(parseTypeRef(elementType))
        }

        // Handle generic types like List<String>, Map<K, V>
        val genericMatch = Regex("""^([a-zA-Z0-9_$.]+)<(.+)>$""").matchEntire(javaType)
        if (genericMatch != null) {
            val rawType = resolveTypeName(genericMatch.groupValues[1])
            val typeArgs = splitTypeArguments(genericMatch.groupValues[2])
            val resolvedArgs = typeArgs.map { parseTypeRef(it.trim()) }
            return TypeRef.Generic(rawType, resolvedArgs)
        }

        // Map Java collection types to Kotlin equivalents
        val resolved = resolveTypeName(javaType)
        return TypeRef.Simple(resolved)
    }

    // --- Only resolve the type name, not generics ---
    private fun resolveTypeName(javaType: String): String {
        when (javaType) {
            "List" -> return "kotlin.collections.List"
            "Map" -> return "kotlin.collections.Map"
            "Set" -> return "kotlin.collections.Set"
            "Collection" -> return "kotlin.collections.Collection"
            "Iterable" -> return "kotlin.collections.Iterable"
            "boolean" -> return "Boolean"
            "byte" -> return "Byte"
            "char" -> return "Char"
            "short" -> return "Short"
            "int" -> return "Int"
            "long" -> return "Long"
            "float" -> return "Float"
            "double" -> return "Double"
            "void" -> return "Unit"
            "String" -> return "String"
            "Object" -> return "Any"
        }
        if (javaType.contains('.')) {
            return javaType
        }
        explicitImports[javaType]?.let { return it }
        nestedClasses[javaType]?.let { return it }
        for (starImport in starImports) {
            val candidateType = "$starImport.$javaType"
            if (classExists(candidateType)) {
                return candidateType
            }
        }
        throw IllegalStateException("Cannot resolve type: $javaType. Available explicit imports: ${explicitImports.keys}, star imports: $starImports, nested classes: ${nestedClasses.keys}")
    }

    // Splits generic type arguments, handling nested generics
    private fun splitTypeArguments(typeArgs: String): List<String> {
        val result = mutableListOf<String>()
        var depth = 0
        var current = StringBuilder()
        for (c in typeArgs) {
            when (c) {
                '<' -> {
                    depth++
                    current.append(c)
                }

                '>' -> {
                    depth--
                    current.append(c)
                }

                ',' -> {
                    if (depth == 0) {
                        result.add(current.toString())
                        current = StringBuilder()
                    } else {
                        current.append(c)
                    }
                }

                else -> current.append(c)
            }
        }
        if (current.isNotEmpty()) {
            result.add(current.toString())
        }
        return result
    }

    private fun classExists(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    private fun extractJavadoc(method: MethodDeclaration): String {
        return method.javadoc.map { it.toText() }.orElse("")
    }
}
