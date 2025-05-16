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

import kotlin.test.*

private const val BEFORE_FLOAT = 1.2345678f
private const val BEFORE = 1.23456789

private const val FIFTH_LONG = 6L
private const val FIFTH_DOUBLE = 6.5
private const val FULL_LONG = FIFTH_LONG * 5
private const val FULL_DOUBLE = FIFTH_DOUBLE * 5
private const val FIFTH_PERCENT_LONG = 100L / 5
private const val FIFTH_PERCENT_DOUBLE = FIFTH_PERCENT_LONG.toDouble()

@Suppress("SpellCheckingInspection")
private val NUMERAL_MAP =
    mapOf(
        1 to "I",
        2 to "II",
        4 to "IV",
        5 to "V",
        9 to "IX",
        10 to "X",
        12 to "XII",
        36 to "XXXVI",
        40 to "XL",
        44 to "XLIV",
        50 to "L",
        72 to "LXXII",
        89 to "LXXXIX",
        90 to "XC",
        95 to "XCV",
        100 to "C",
        321 to "CCCXXI",
        400 to "CD",
        422 to "CDXXII",
        500 to "D",
        623 to "DCXXIII",
        900 to "CM",
        913 to "CMXIII",
        1000 to "M",
        2000 to "MM",
        3000 to "MMM",
        3999 to "MMMCMXCIX",
    )

class PrimitiveFunctionsTest {
    @Test
    fun `Float decimal places`() {
        val last = BEFORE_FLOAT.toString().last().digitToInt() - 1
        for (test in 1 until last + 1) {
            assertTrue(
                actual =
                    BEFORE_FLOAT.setDecimalPlaces(test)
                        .toString()
                        .endsWith((test + if (test >= 3 && test != last) 2 else 1).toString()),
                message =
                    "Decimal places: $test. " +
                        "Result: ${BEFORE_FLOAT.setDecimalPlaces(test)}. " +
                        "Expected (ends with): ${test + if (test >= 3 && test != last) 2 else 1}",
            )
        }
    }

    @Test
    fun `Double decimal places`() {
        val last = BEFORE.toString().last().digitToInt() - 1
        for (test in 1 until last + 1) {
            assertTrue(
                actual =
                    BEFORE.setDecimalPlaces(test)
                        .toString()
                        .endsWith((test + if (test >= 3 && test != last) 2 else 1).toString()),
                message =
                    "Decimal places: $test. " +
                        "Result: ${BEFORE.setDecimalPlaces(test)}. " +
                        "Expected (ends with): ${test + if (test >= 3 && test != last) 2 else 1}",
            )
        }
    }

    @Test
    fun `Get percentage`() {
        assertEquals(FIFTH_PERCENT_LONG, FIFTH_LONG.getPercentage(FULL_LONG))
        assertEquals(FIFTH_PERCENT_DOUBLE, FIFTH_DOUBLE.getPercentage(FULL_DOUBLE))
    }

    @Test
    fun `Roman numeral`() {
        for ((num, numeral) in NUMERAL_MAP) {
            assertEquals(numeral, num.toRomanNumeral())
        }
        assertEquals("3000", 3000.toRomanNumeral(2999))
        assertNotEquals("3000", 3000.toRomanNumeral(3000))
    }
}
