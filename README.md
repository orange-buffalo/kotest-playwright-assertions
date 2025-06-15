# kotest-playwright-assertions

[![](https://maven-badges.herokuapp.com/maven-central/io.orange-buffalo/kotest-playwright-assertions/badge.svg?style=flat-square)](https://search.maven.org/artifact/io.orange-buffalo/kotest-playwright-assertions)

**kotest-playwright-assertions** provides [Kotest](https://kotest.io/)-style assertions
for [Playwright](https://playwright.dev/).

> [!IMPORTANT]
> This library does not implement any assertions itself. It only wraps the Playwright assertions to provide a
> Kotest-style API.
> The assertions are generated based on the latest Playwright Java API. This means that if your project uses an older
> version of Playwright, those newer assertions will fail with `NoSuchMethodError` in runtime.

## Getting Started

### 1. Add Dependency

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    testImplementation("io.orange-buffalo:kotest-playwright-assertions:$kotestPlaywrightAssertionsVersion")
}
```

or to your `pom.xml`:

```xml

<dependency>
  <groupId>io.orange-buffalo</groupId>
  <artifactId>kotest-playwright-assertions</artifactId>
  <version>${kotest-playwright-assertions.version}</version>
  <scope>test</scope>
</dependency>
```

The latest version can be taken
from [Maven Central](https://search.maven.org/search?q=g:io.orange-buffalo%20a:kotest-playwright-assertions).

### 2. Usage Example

```kotlin
import io.orangebuffalo.kotestplaywrightassertions
import com.microsoft.playwright.Page

...
val page: Page = ...
page.locator("#welcome").shouldHaveText("Welcome!")
```

### 3. Available Assertions

All assertions available in the latest version of Playwright are supported, with the following mapping:

- `has*` -> `shouldHave*`
- `contains*` -> `shouldContain*`
- `matches*` -> `shouldMatch*`
- `is*` -> `shouldBe*`

All of them have also `not` variants, e.g.: `shouldNotHave*`, `shouldNotContain*`, etc.

## Compatibility

- Kotlin 1.8+
- Kotest 5.x
- Playwright for Java 1.30+
