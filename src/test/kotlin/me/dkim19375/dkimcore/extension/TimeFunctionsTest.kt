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

class TimeFunctionsTest {
    @Test
    fun `getTimeFormatted(short = true)`() {
        val tests =
            mapOf(
                0L to "0s",
                100L to "1m 40s",
                3600L to "1h 0m 0s",
                3930L to "1h 5m 30s",
                86400L to "1d 0h 0m 0s",
                90061L to "1d 1h 1m 1s",
                139482718L to "1614d 9h 11m 58s",
            )
        for ((input, expected) in tests) {
            val result = input.getTimeFormatted(short = true)
            assertEquals(
                expected,
                result,
                "Expected <$expected> from $input.getTimeFormatted(short = true), actual <$result>.",
            )
        }
    }

    @Test
    fun `getTimeFormatted(short = false)`() {
        val testsLong =
            mapOf(
                0L to "0 Seconds",
                100L to "1 Minute, and 40 Seconds",
                3600L to "1 Hour, 0 Minutes, and 0 Seconds",
                3930L to "1 Hour, 5 Minutes, and 30 Seconds",
                86400L to "1 Day, 0 Hours, 0 Minutes, and 0 Seconds",
                90061L to "1 Day, 1 Hour, 1 Minute, and 1 Second",
                139482718L to "1614 Days, 9 Hours, 11 Minutes, and 58 Seconds",
            )
        for ((input, expected) in testsLong) {
            val result = input.getTimeFormatted()
            assertEquals(
                expected,
                result,
                "Expected <$expected> from $input.getTimeFormatted(), actual <$result>.",
            )
        }
    }
}
