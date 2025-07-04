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

import java.awt.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ColorFunctionsTest {
    @Test
    fun `Color name to Color`() {
        assertEquals(Color.RED, "RED".getColor())
        for ((colorName, color) in getColorsNameMap()) {
            assertEquals(color, colorName.getColor())
        }
    }

    @Test
    fun `Hex to Color`() {
        assertEquals(Color.RED, "FF0000".getColor())
        assertEquals(Color.PINK, "#ffafaf".getColor())
        assertNull("#FFFFFG".getColor())
    }

    @Test
    fun `RGB to Color`() {
        assertEquals(Color.PINK, "(255,175   ,  175)".getColor())
        assertEquals(Color.PINK, "255  175  175".getColor())
    }
}
