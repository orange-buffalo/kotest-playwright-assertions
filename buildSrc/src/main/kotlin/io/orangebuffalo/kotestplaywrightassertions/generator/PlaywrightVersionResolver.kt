package io.orangebuffalo.kotestplaywrightassertions.generator

class PlaywrightVersionResolver {
    
    fun resolveVersion(version: String): String {
        return when {
            version.endsWith("-SNAPSHOT") -> "main"
            version.startsWith("v") -> version
            else -> "v$version"
        }
    }
}
