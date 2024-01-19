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

package me.dkim19375.dkimcore.collection

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class LazyReadOnlyMapTest {
    private object LazyAccessException : RuntimeException() {
        private fun readResolve(): Any = LazyAccessException
    }

    private object ThrowOnLoadLazy : Lazy<Unit> {
        override val value: Unit
            get() = throw LazyAccessException

        override fun isInitialized(): Boolean = false
    }

    @Test
    fun `Does not throw while iterating`() {
        val map = LazyReadOnlyMap(mapOf(1 to ThrowOnLoadLazy, 2 to ThrowOnLoadLazy))
        assertDoesNotThrow {
            for (entry in map) {
                entry.key
            }
        }
    }

    @Test
    fun `Does not throw while looping entries`() {
        val map = LazyReadOnlyMap(mapOf(1 to ThrowOnLoadLazy, 2 to ThrowOnLoadLazy))
        assertDoesNotThrow {
            for (entry in map.entries) {
                entry.key
            }
        }
    }

    @Test
    fun `Does not throw while looping keys`() {
        val map = LazyReadOnlyMap(mapOf(1 to ThrowOnLoadLazy, 2 to ThrowOnLoadLazy))
        var sum = 0
        assertDoesNotThrow {
            for (num in map.keys) {
                sum += num
            }
        }
    }

    @Test
    fun `Throws when getting`() {
        val map = LazyReadOnlyMap(mapOf(1 to ThrowOnLoadLazy, 2 to ThrowOnLoadLazy))
        assertThrows<LazyAccessException> {
            map[1]
        }
    }

    @Test
    fun `Throws when getting value of entry`() {
        val map = LazyReadOnlyMap(mapOf(1 to ThrowOnLoadLazy, 2 to ThrowOnLoadLazy))
        assertThrows<LazyAccessException> {
            map.entries.first().value
        }
    }

}