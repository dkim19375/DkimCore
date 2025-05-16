/*
 * MIT License
 *
 * Copyright (c) 2023 dkim19375
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dkim19375.dkimcore.extension

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

private val TEST_UUID = UUID.randomUUID()
private const val PLACEHOLDER_STR = "Testing placeholder_1 and placeholder 2!"
private const val PERCENT_PLACEHOLDER_STR = "Testing %placeholder_1% and %placeholder 2%!"
private const val CUSTOM_PLACEHOLDER_STR =
    "Testing (placeholder_1) and (placeholder 2)! *(no_parse)"
private const val EXPECTED_PLACEHOLDER = "Testing replacement_1 and replacement 2!"
private val PLACEHOLDERS =
    mapOf(
        "placeholder_1" to "replacement_1",
        "placeholder 2" to "replacement 2",
        "no_parse" to "parsed",
    )

class StringFunctionsTest {
    @Test
    fun `toString to UUID`() {
        val value = TEST_UUID.toString().toUUID()
        assertNotNull(value)
        assertEquals(TEST_UUID, value)
    }

    @Test
    fun `UUID without dashes to UUID`() {
        val value = TEST_UUID.toString().replace("-", "").toUUID()
        assertNotNull(value)
        assertEquals(TEST_UUID, value)
    }

    @Test
    fun `UUID without dashes with spaces to UUID`() {
        val value = TEST_UUID.toString().replace("-", " ").toUUID()
        assertNotNull(value)
        assertEquals(TEST_UUID, value)
    }

    @Test
    fun `Set placeholders`() {
        assertEquals(EXPECTED_PLACEHOLDER, PLACEHOLDER_STR.setPlaceholders(PLACEHOLDERS, false))
        assertNotEquals(EXPECTED_PLACEHOLDER, PLACEHOLDER_STR.setPlaceholders(PLACEHOLDERS))
        assertEquals(EXPECTED_PLACEHOLDER, PERCENT_PLACEHOLDER_STR.setPlaceholders(PLACEHOLDERS))
        assertNotEquals(
            EXPECTED_PLACEHOLDER,
            PERCENT_PLACEHOLDER_STR.setPlaceholders(PLACEHOLDERS, false),
        )
        assertEquals(
            "$EXPECTED_PLACEHOLDER (no_parse)",
            CUSTOM_PLACEHOLDER_STR.setPlaceholders(
                map = PLACEHOLDERS,
                prefix = "(",
                suffix = ")",
                escape = '*',
            ),
        )
    }
}
