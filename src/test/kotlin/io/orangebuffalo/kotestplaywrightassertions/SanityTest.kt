package io.orangebuffalo.kotestplaywrightassertions

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.LocatorAssertions
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain

class SanityTest : FunSpec({

    lateinit var playwright: Playwright
    lateinit var browser: Browser
    lateinit var page: Page

    beforeSpec {
        playwright = Playwright.create()
        browser = playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(true))
        page = browser.newPage()
    }

    afterSpec {
        page.close()
        browser.close()
        playwright.close()
    }

    test("should assert element is visible using generated extension") {
        page.navigate("https://github.com/orange-buffalo/kotest-playwright-assertions")
        val testLocator = page.getByText("kotest-playwright-assertions").first()
        testLocator.shouldBeVisible()
    }

    test("should assert element is visible using generated extension (with config)") {
        page.navigate("https://github.com/orange-buffalo/kotest-playwright-assertions")
        val testLocator = page.getByText("kotest-playwright-assertions").first()
        testLocator.shouldBeVisible(
            LocatorAssertions
                .IsVisibleOptions()
                .setTimeout(5_000.0)
        )
    }

    test("should throw exception when asserting non-existing element is visible") {
        page.navigate("https://github.com/orange-buffalo/kotest-playwright-assertions")
        val nonExistingElement = page.locator("#this-element-definitely-does-not-exist")
        shouldThrow<AssertionError> {
            nonExistingElement.shouldBeVisible()
        }.message.shouldNotBeNull().shouldContain("Locator expected to be visible")
    }

    test("should throw exception when asserting non-existing element is visible (with config)") {
        page.navigate("https://github.com/orange-buffalo/kotest-playwright-assertions")
        val nonExistingElement = page.locator("#this-element-definitely-does-not-exist")
        shouldThrow<AssertionError> {
            nonExistingElement.shouldBeVisible(
                LocatorAssertions
                    .IsVisibleOptions()
                    .setTimeout(2_000.0)
            )
        }.message.shouldNotBeNull().shouldContain("Locator expected to be visible")
    }

    test("should assert element is not visible using generated extension") {
        page.navigate("https://github.com/orange-buffalo/kotest-playwright-assertions")
        val hiddenLocator = page.locator("#this-element-definitely-does-not-exist")
        hiddenLocator.shouldNotBeVisible()
    }

    test("should assert element is not visible using generated extension (with config)") {
        page.navigate("https://github.com/orange-buffalo/kotest-playwright-assertions")
        val hiddenLocator = page.locator("#this-element-definitely-does-not-exist")
        hiddenLocator.shouldNotBeVisible(
            LocatorAssertions
                .IsVisibleOptions()
                .setTimeout(2_000.0)
        )
    }
})
