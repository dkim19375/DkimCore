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

package me.dkim19375.dkimcore.misc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertTrue
import me.dkim19375.dkimcore.misc.WeightedRandomPicker.NoItemsException

class WeightedRandomPickerTest {
    private fun getPicker(): WeightedRandomPicker<String> =
        WeightedRandomPicker(mapOf("A" to 1, "B" to 2, "C" to 3))

    @Test
    fun `Throws with no items`() {
        val picker = WeightedRandomPicker<String>(emptyMap())
        assertFailsWith<NoItemsException>(block = picker::pickItem)
    }

    @Test
    fun `Doesn't remove item after picking`() {
        val picker = getPicker()
        assertEquals(3, picker.getRemainingItems().size)
        picker.pickItem()
        assertEquals(3, picker.getRemainingItems().size)
    }

    @Test
    fun `Removes item after picking`() {
        val picker = getPicker()
        assertEquals(3, picker.getRemainingItems().size)
        val picked = picker.pickItem(removeResult = true)
        assertEquals(2, picker.getRemainingItems().size)
        assertTrue(picker.getRemainingItems().none { it.first == picked })
    }

    @Test
    fun `Test getRemainingItems()`() {
        val set = mutableSetOf("A" to 0.1, "B" to 0.2, "C" to 0.3)
        val picker = WeightedRandomPicker(set)
        assertEquals(set, picker.getRemainingItems())
        assertNotSame(set, picker.getRemainingItems())
        val picked = picker.pickItem(removeResult = true)
        set.removeIf { it.first == picked }
        assertEquals(set, picker.getRemainingItems())
    }
}
