package io.orangebuffalo.kotestplaywrightassertions.generator

class JavaDocTransformer {
    
    fun transformJavaDoc(javadoc: String): String {
        if (javadoc.isBlank()) return ""
        
        var result = javadoc
        
        // Step 1: Remove JavaDoc comment markers first
        result = result
            .replace(Regex("^\\s*/\\*\\*\\s*", RegexOption.MULTILINE), "")
            .replace(Regex("\\s*\\*/\\s*$", RegexOption.MULTILINE), "")
            // Use [ \t] instead of \s to avoid matching newlines
            .replace(Regex("^[ \\t]*\\*[ \\t]?", RegexOption.MULTILINE), "")
        
        // Step 2: Convert HTML elements to markdown
        result = result
            // Convert HTML links to markdown links
            .replace(Regex("<a\\s+href=\"([^\"]+)\">([^<]+)</a>", RegexOption.IGNORE_CASE)) { 
                "[${it.groupValues[2]}](${it.groupValues[1]})"
            }
            // Convert <strong> tags to markdown bold
            .replace(Regex("<strong>\\s*", RegexOption.IGNORE_CASE), "**")
            .replace(Regex("\\s*</strong>", RegexOption.IGNORE_CASE), "**")
            // Convert <p> tags to paragraph breaks
            .replace(Regex("<p>\\s*", RegexOption.IGNORE_CASE), "\n\n")
        
        // Step 3: Convert JavaDoc tags
        result = result
            // Convert <pre>{@code ...}</pre> blocks to markdown code blocks
            .replace(Regex("<pre>\\s*\\{@code\\s*([^}]+)\\s*}\\s*</pre>", RegexOption.DOT_MATCHES_ALL)) { 
                "```\n${it.groupValues[1].trim()}\n```"
            }
            // Convert inline {@code ...} to backticks
            .replace(Regex("\\{@code ([^}]+)}")) { "`${it.groupValues[1]}`" }
        
        // Step 4: Normalize whitespace
        result = result
            // Clean up multiple consecutive newlines (but keep double newlines for paragraphs)
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
        
        return result
    }
}
