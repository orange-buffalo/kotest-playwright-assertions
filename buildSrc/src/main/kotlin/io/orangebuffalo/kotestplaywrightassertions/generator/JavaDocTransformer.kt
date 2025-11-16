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
            // Convert <ol> and </ol> - just remove them as markdown will auto-number
            .replace(Regex("</?ol>\\s*", RegexOption.IGNORE_CASE), "\n")
            // Convert <li> tags to markdown list items
            .replace(Regex("[ \\t]*<li>\\s*", RegexOption.IGNORE_CASE), "- ")
            .replace(Regex("\\s*</li>\\s*", RegexOption.IGNORE_CASE), "\n")
            // Convert <p> tags to paragraph breaks
            .replace(Regex("<p>\\s*", RegexOption.IGNORE_CASE), "\n\n")
        
        // Step 3: Convert JavaDoc tags
        result = result
            // Convert <pre>{@code ...}</pre> blocks to markdown code blocks
            // Use non-greedy match (.+?) to properly handle braces in the code
            .replace(Regex("<pre>\\s*\\{@code\\s*(.+?)\\s*}\\s*</pre>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))) { 
                "```\n${it.groupValues[1].trim()}\n```"
            }
            // Convert {@link Class#method ...} to just the method name in backticks
            // Format: {@link package.Class#method() Description} or {@link Class#method}
            .replace(Regex("\\{@link\\s+([^}]+)}")) { match ->
                val content = match.groupValues[1].trim()
                // Extract the readable part (after the class/method reference)
                val parts = content.split(Regex("\\s+"), 2)
                if (parts.size > 1) {
                    // Has description: use it
                    parts[1]
                } else {
                    // No description: extract method/class name
                    val ref = parts[0]
                    when {
                        ref.contains("#") -> {
                            // Has method: extract method name
                            val methodPart = ref.substringAfter("#").substringBefore("(")
                            "`$methodPart()`"
                        }
                        ref.contains(".") -> {
                            // Just class: extract simple name
                            "`${ref.substringAfterLast(".")}`"
                        }
                        else -> "`$ref`"
                    }
                }
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
