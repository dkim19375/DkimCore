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
            message = "Expected time to be less than ${TIME_LIMIT}ms but was ${result.first}ms"
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
            message = "Expected time to be less than ${TIME_LIMIT}ms but was ${result.first}ms"
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