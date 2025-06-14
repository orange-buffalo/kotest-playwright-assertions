package io.orangebuffalo.kotestplaywrightassertions.generator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JavaDocTransformerTest : FunSpec({

    val transformer = JavaDocTransformer()

    test("remove me") {
        // no op, just keeping the pre-generated test
    }

//    test("should convert {@code ...} to backticks") {
//        val input = "Use {@code locator.isVisible()} to check visibility"
//        val expected = "Use `locator.isVisible()` to check visibility"
//
//        transformer.transformJavaDoc(input) shouldBe expected
//    }
//
//    test("should convert <strong> tags to markdown bold") {
//        val input = "This is <strong>important</strong> text"
//        val expected = "This is **important** text"
//
//        transformer.transformJavaDoc(input) shouldBe expected
//    }
//
//    test("should convert <p> tags to double newlines") {
//        val input = "First paragraph.<p>Second paragraph."
//        val expected = "First paragraph.\n\nSecond paragraph."
//
//        transformer.transformJavaDoc(input) shouldBe expected
//    }
//
//    test("should convert complex pre code blocks with Java note") {
//        val input = """
//            Example usage:
//            <pre>{@code
//            locator.isVisible();
//            assertThat(page).hasTitle("Title");
//            }</pre>
//            More text.
//        """.trimIndent()
//
//        val result = transformer.transformJavaDoc(input)
//        result shouldBe """
//            Example usage:
//            ```
//            locator.isVisible();
//            assertThat(page).hasTitle("Title");
//            ```
//            *Note: This example is Java-specific*
//            More text.
//        """.trimIndent()
//    }
//
//    test("should handle complex real-world javadoc") {
//        val input = """
//            /**
//             * Ensures that {@code Locator} points to an element that contains the given text. You can use regular expressions for the value as well.
//             *
//             * <p><strong>Usage</strong>
//             *
//             * <pre>{@code
//             * assertThat(page.getByRole(AriaRole.BUTTON)).containsText("Sign in");
//             * }</pre>
//             *
//             * @param expected Expected substring or RegExp.
//             * @since v1.20
//             */
//        """.trimIndent()
//
//        val result = transformer.transformJavaDoc(input)
//        result shouldBe """
//            Ensures that `Locator` points to an element that contains the given text. You can use regular expressions for the value as well.
//
//            **Usage**
//            ```
//            assertThat(page.getByRole(AriaRole.BUTTON)).containsText("Sign in");
//            ```
//            *Note: This example is Java-specific*
//
//            @param expected Expected substring or RegExp.
//            @since v1.20
//        """.trimIndent()
//    }
//
//    test("should return empty string for blank javadoc") {
//        transformer.transformJavaDoc("") shouldBe ""
//        transformer.transformJavaDoc("   ") shouldBe ""
//    }
})
