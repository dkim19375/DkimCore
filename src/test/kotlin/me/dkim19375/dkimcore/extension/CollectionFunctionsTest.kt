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

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.*

private val STRING_LIST: List<String> = listOf("a", "B", "c")
private val STRING_SET: Set<String> = setOf("a", "b", "c")
private val STRING_MAP: Map<String, String> = mapOf("a" to "b", "c" to "d")
private val UUID_LIST: List<UUID> = (1..10).map { UUID.randomUUID() }
private val LIST_LIST: List<List<Int>> = listOf(listOf(1, 2, 3), listOf(4, 5, 6))
private val EXPECTED_LIST: List<Int> = listOf(1, 2, 3, 4, 5, 6)
private val MAP_LIST: List<Map<Int, Int>> = listOf(mapOf(1 to 2, 3 to 4), mapOf(5 to 6, 7 to 8))
private val EXPECTED_MAP: Map<Int, Int> = mapOf(1 to 2, 3 to 4, 5 to 6, 7 to 8)
private val UNSPLIT_LIST: List<Int> = (1..20).toList()
private val SPLIT_LIST: List<List<Int>> = listOf(
    (1..6).toList(),
    (7..12).toList(),
    (13..18).toList(),
    listOf(19, 20)
)
private val SPLIT_LIMITED_LIST: List<List<Int>> = listOf(
    (1..6).toList(),
    (7..12).toList(),
    (13..20).toList(),
)

class CollectionFunctionsTest {
    @Test
    fun `Contains ignore case`() {
        assertTrue(STRING_LIST.containsIgnoreCase("a"))
        assertTrue(STRING_LIST.containsIgnoreCase("A"))
        assertFalse(STRING_LIST.containsIgnoreCase("d"))
    }

    @Test
    fun `Get ignore case`() {
        assertEquals(STRING_LIST.getIgnoreCase("a"), "a")
        assertEquals(STRING_LIST.getIgnoreCase("b"), "B")
        assertNull(STRING_LIST.getIgnoreCase("d"))
    }

    @Test
    fun `Map removeIf`() {
        val map = STRING_MAP.toMutableMap()
        map.removeIf { k, v ->
            k == "a" && v == "b"
        }
        assertEquals(map, mapOf("c" to "d"))
    }

    @Test
    fun `Get random UUID`() {
        assertFalse(UUID_LIST.getRandomUUID() in UUID_LIST)
    }

    @Test
    fun `Combine list`() {
        assertContentEquals(LIST_LIST.combine(), EXPECTED_LIST)
    }

    @Test
    fun `Combine map`() {
        assertEquals(MAP_LIST.combine(), EXPECTED_MAP)
    }

    @Test
    fun `Immutable set`() {
        assertThrows<UnsupportedOperationException> {
            (STRING_SET.toImmutableSet() as MutableSet<String>).add("d")
        }
        assertThrows<UnsupportedOperationException> {
            (immutableSetOf("a") as MutableSet<String>).add("b")
        }
        assertThrows<UnsupportedOperationException> {
            (immutableSetOf(setOf("a")) as MutableSet<String>).add("b")
        }
    }

    @Test
    fun `Immutable list`() {
        assertThrows<UnsupportedOperationException> {
            (STRING_LIST.toImmutableList() as MutableList<String>).add("d")
        }
        assertThrows<UnsupportedOperationException> {
            (immutableListOf("a") as MutableList<String>).add("b")
        }
        assertThrows<UnsupportedOperationException> {
            (immutableListOf(listOf("a")) as MutableList<String>).add("b")
        }
    }

    @Test
    fun `Immutable map`() {
        assertThrows<UnsupportedOperationException> {
            (STRING_MAP.toImmutableMap() as MutableMap<String, String>)["e"] = "f"
        }
        assertThrows<UnsupportedOperationException> {
            (immutableMapOf("a" to "b") as MutableMap<String, String>)["c"] = "d"
        }
        assertThrows<UnsupportedOperationException> {
            (immutableMapOf(listOf("a" to "b")) as MutableMap<String, String>)["c"] = "d"
        }
        assertThrows<UnsupportedOperationException> {
            (listOf("a" to "b").toImmutableMap() as MutableMap<String, String>)["c"] = "d"
        }
    }

    @Test
    fun `Concurrent set`() {
        assertDoesNotThrow {
            val set = concurrentSetOf(1, 2, 3)
            for (i in set) {
                set.remove(i)
            }
        }
    }

    @Test
    fun `Concurrent Deque`() {
        assertDoesNotThrow {
            val deque = listOf(1, 2, 3).toConcurrentDeque()
            for (i in deque) {
                deque.remove(i)
            }
        }
    }

    @Test
    fun `Concurrent map`() {
        assertDoesNotThrow {
            val map = mapOf(1 to 2, 3 to 4, 5 to 6).toConcurrentMap()
            for (i in map) {
                map.remove(i.key)
            }
        }
    }

    @Test
    fun `Split list`() {
        assertContentEquals(UNSPLIT_LIST.split(6), SPLIT_LIST)
        assertDoesNotThrow { UNSPLIT_LIST.split(21) }
        assertContentEquals(UNSPLIT_LIST.split(21), listOf(UNSPLIT_LIST))
    }

    @Test
    fun `Split list with limit`() {
        assertContentEquals(UNSPLIT_LIST.split(6, 3), SPLIT_LIMITED_LIST)
    }
}