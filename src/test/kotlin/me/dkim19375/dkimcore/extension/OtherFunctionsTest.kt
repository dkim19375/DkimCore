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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val SLEEP_TIME = 100L
private const val TIME_LIMIT = 300L

class OtherFunctionsTest {
    private class TestException : RuntimeException()

    private enum class TestEnum {
        FIRST,
        SECOND,
    }

    @Test
    fun `Run catching or null`() {
        assertNull(runCatchingOrNull { throw TestException() })
        assertEquals("abc", runCatchingOrNull { "abc" })
    }

    @Test
    fun `Test getMillisAndValue`() {
        val result = getMillisAndValue {
            Thread.sleep(SLEEP_TIME)
            "abc"
        }
        assertTrue(
            actual = result.first <= TIME_LIMIT,
            message = "Expected time to be less than ${TIME_LIMIT}ms but was ${result.first}ms",
        )
        assertEquals("abc", result.second)
    }

    @Test
    fun `Test getMillisLongAndValue`() {
        val result = getMillisLongAndValue {
            Thread.sleep(SLEEP_TIME)
            "abc"
        }
        assertTrue(
            actual = result.first <= TIME_LIMIT,
            message = "Expected time to be less than ${TIME_LIMIT}ms but was ${result.first}ms",
        )
        assertEquals("abc", result.second)
    }

    @Test
    fun `Enum Value of or Null`() {
        assertEquals(TestEnum.FIRST, enumValueOfOrNull(TestEnum.FIRST.name))
        assertEquals(TestEnum.SECOND, enumValueOfOrNull(TestEnum.SECOND.name.lowercase()))
        assertNull(enumValueOfOrNull<TestEnum>("Test"))
        assertNull(enumValueOfOrNull<TestEnum>("FIRSTa"))
    }
}
