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

class PrimitiveFunctionsTest {
    @Test
    fun `Float decimal places`() {
        for (test in 1 until BEFORE_FLOAT.toString().last().digitToInt()) {
            assertTrue(
                actual = BEFORE_FLOAT.setDecimalPlaces(test).toString()
                    .endsWith((test + if (test >= 3 && test != 8) 2 else 1).toString()),
                message = "Decimal places: $test. " +
                        "Result: ${BEFORE_FLOAT.setDecimalPlaces(test)}. " +
                        "Expected (ends with): ${test + if (test >= 3 && test != 8) 2 else 1}"
            )
        }
    }

    @Test
    fun `Double decimal places`() {
        for (test in 1 until BEFORE.toString().last().digitToInt()) {
            assertTrue(
                actual = BEFORE.setDecimalPlaces(test).toString()
                    .endsWith((test + if (test >= 3 && test != 8) 2 else 1).toString()),
                message = "Decimal places: $test. " +
                        "Result: ${BEFORE.setDecimalPlaces(test)}. " +
                        "Expected (ends with): ${test + if (test >= 3 && test != 8) 2 else 1}"
            )
        }
    }
}