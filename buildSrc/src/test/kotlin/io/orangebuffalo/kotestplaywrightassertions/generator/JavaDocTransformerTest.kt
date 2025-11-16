package io.orangebuffalo.kotestplaywrightassertions.generator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JavaDocTransformerTest : FunSpec({

    val transformer = JavaDocTransformer()

    test("should convert {@code ...} to backticks") {
        val input = "Use {@code locator.isVisible()} to check visibility"
        val expected = "Use `locator.isVisible()` to check visibility"

        transformer.transformJavaDoc(input) shouldBe expected
    }

    test("should convert <strong> tags to markdown bold") {
        val input = "This is <strong>important</strong> text"
        val expected = "This is **important** text"

        transformer.transformJavaDoc(input) shouldBe expected
    }

    test("should convert <p> tags to double newlines") {
        val input = "First paragraph.<p>Second paragraph."
        val expected = "First paragraph.\n\nSecond paragraph."

        transformer.transformJavaDoc(input) shouldBe expected
    }

    test("should convert HTML links to markdown links") {
        val input = """See <a href="https://example.com/docs">documentation</a> for details"""
        val expected = """See [documentation](https://example.com/docs) for details"""

        transformer.transformJavaDoc(input) shouldBe expected
    }

    test("should convert complex pre code blocks") {
        val input = """
            Example usage:
            <pre>{@code
            locator.isVisible();
            assertThat(page).hasTitle("Title");
            }</pre>
            More text.
        """.trimIndent()

        val result = transformer.transformJavaDoc(input)
        result shouldBe """
            Example usage:
            ```
            locator.isVisible();
            assertThat(page).hasTitle("Title");
            ```
            More text.
        """.trimIndent()
    }

    test("should handle complex real-world javadoc") {
        val input = """
            /**
             * Ensures that {@code Locator} points to an element that contains the given text. You can use regular expressions for the value as well.
             *
             * <p><strong>Usage</strong>
             *
             * <pre>{@code
             * assertThat(page.getByRole(AriaRole.BUTTON)).containsText("Sign in");
             * }</pre>
             *
             * @param expected Expected substring or RegExp.
             * @since v1.20
             */
        """.trimIndent()

        val result = transformer.transformJavaDoc(input)
        result shouldBe """
            Ensures that `Locator` points to an element that contains the given text. You can use regular expressions for the value as well.

            **Usage**

            ```
            assertThat(page.getByRole(AriaRole.BUTTON)).containsText("Sign in");
            ```

            @param expected Expected substring or RegExp.
            @since v1.20
        """.trimIndent()
    }

    test("should handle javadoc with HTML links and code tags") {
        val input = """
            /**
             * Ensures the {@code Locator} points to a disabled element. Element is disabled if it has "disabled" attribute or is
             * disabled via <a href="https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-disabled">'aria-disabled'</a>.
             * Note that only native control elements such as HTML {@code button}, {@code input}, {@code select}, {@code textarea},
             * {@code option}, {@code optgroup} can be disabled by setting "disabled" attribute.
             *
             * <p><strong>Usage</strong>
             *
             * <pre>{@code
             * assertThat(page.locator("button.submit")).isDisabled();
             * }</pre>
             *
             * @since v1.20
             */
        """.trimIndent()

        val result = transformer.transformJavaDoc(input)
        result shouldBe """
            Ensures the `Locator` points to a disabled element. Element is disabled if it has "disabled" attribute or is
            disabled via ['aria-disabled'](https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-disabled).
            Note that only native control elements such as HTML `button`, `input`, `select`, `textarea`,
            `option`, `optgroup` can be disabled by setting "disabled" attribute.

            **Usage**

            ```
            assertThat(page.locator("button.submit")).isDisabled();
            ```

            @since v1.20
        """.trimIndent()
    }

    test("should convert HTML ordered lists to markdown") {
        val input = """
            /**
             * Requirements:
             * <ol>
             * <li> First item</li>
             * <li> Second item</li>
             * <li> Third item</li>
             * </ol>
             * End of list.
             */
        """.trimIndent()

        val result = transformer.transformJavaDoc(input)
        result shouldBe """
            Requirements:

            - First item
            - Second item
            - Third item
            End of list.
        """.trimIndent()
    }

    test("should return empty string for blank javadoc") {
        transformer.transformJavaDoc("") shouldBe ""
        transformer.transformJavaDoc("   ") shouldBe ""
    }
})
