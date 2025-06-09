package io.orangebuffalo.kotestplaywrightassertions.generator

class JavaDocTransformer {
    
    fun transformJavaDoc(javadoc: String): String {
        if (javadoc.isBlank()) return ""
        
        return javadoc
            .replace(Regex("\\{@code ([^}]+)}")) { "`${it.groupValues[1]}`" }
            .replace("<strong>", "**")
            .replace("</strong>", "**")
            .replace(Regex("<p>\\s*")) { "\n\n" }
            .replace(Regex("<pre>\\s*\\{@code\\s*([^}]+)\\s*}\\s*</pre>", RegexOption.DOT_MATCHES_ALL)) { 
                "\n```\n${it.groupValues[1].trim()}\n```\n*Note: This example is Java-specific*\n"
            }
            .replace(Regex("\\s*\\*\\s*", RegexOption.MULTILINE), " ")
            .replace(Regex("^\\s*/\\*\\*\\s*", RegexOption.MULTILINE), "")
            .replace(Regex("\\s*\\*/\\s*$", RegexOption.MULTILINE), "")
            .trim()
    }
}
