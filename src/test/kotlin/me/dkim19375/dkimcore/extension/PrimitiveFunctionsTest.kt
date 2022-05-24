/*
 * MIT License
 *
 * Copyright (c) 2021 dkim19375
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

import kotlin.test.*

private const val BEFORE_FLOAT = 1.2345678f
private const val BEFORE = 1.23456789

private const val FIFTH_LONG = 6L
private const val FIFTH_DOUBLE = 6.5
private const val FULL_LONG = FIFTH_LONG * 5
private const val FULL_DOUBLE = FIFTH_DOUBLE * 5
private const val FIFTH_PERCENT_LONG = 100L / 5
private const val FIFTH_PERCENT_DOUBLE = FIFTH_PERCENT_LONG.toDouble()

class PrimitiveFunctionsTest {
    @Test
    fun `Float decimal places`() {
        val last = BEFORE_FLOAT.toString().last().digitToInt() - 1
        for (test in 1 until last + 1) {
            assertTrue(
                actual = BEFORE_FLOAT.setDecimalPlaces(test).toString()
                    .endsWith((test + if (test >= 3 && test != last) 2 else 1).toString()),
                message = "Decimal places: $test. " +
                        "Result: ${BEFORE_FLOAT.setDecimalPlaces(test)}. " +
                        "Expected (ends with): ${test + if (test >= 3 && test != last) 2 else 1}"
            )
        }
    }

    @Test
    fun `Double decimal places`() {
        val last = BEFORE.toString().last().digitToInt() - 1
        for (test in 1 until last + 1) {
            assertTrue(
                actual = BEFORE.setDecimalPlaces(test).toString()
                    .endsWith((test + if (test >= 3 && test != last) 2 else 1).toString()),
                message = "Decimal places: $test. " +
                        "Result: ${BEFORE.setDecimalPlaces(test)}. " +
                        "Expected (ends with): ${test + if (test >= 3 && test != last) 2 else 1}"
            )
        }
    }

    @Test
    fun `Get percentage`() {
        assertEquals(FIFTH_PERCENT_LONG, FIFTH_LONG.getPercentage(FULL_LONG))
        assertEquals(FIFTH_PERCENT_DOUBLE, FIFTH_DOUBLE.getPercentage(FULL_DOUBLE))
    }
}