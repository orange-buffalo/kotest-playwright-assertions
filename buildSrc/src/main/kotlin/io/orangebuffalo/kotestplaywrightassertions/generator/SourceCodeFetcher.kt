package io.orangebuffalo.kotestplaywrightassertions.generator

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

class SourceCodeFetcher {
    
    private val client = HttpClient(CIO)
    
    fun fetchLocatorAssertions(version: String): String = runBlocking {
        val url = "https://raw.githubusercontent.com/microsoft/playwright-java/$version/playwright/src/main/java/com/microsoft/playwright/assertions/LocatorAssertions.java"
        
        val response = client.get(url)
        if (response.status.value !in 200..299) {
            throw IllegalStateException("Failed to fetch source code from $url: ${response.status}")
        }
        
        response.bodyAsText()
    }
}
