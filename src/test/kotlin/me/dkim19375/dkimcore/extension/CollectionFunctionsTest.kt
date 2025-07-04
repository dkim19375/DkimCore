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

import java.util.Deque
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.junit.jupiter.api.assertDoesNotThrow

private val STRING_LIST: List<String> = listOf("a", "B", "c")
private val STRING_SET: Set<String> = setOf("a", "b", "c")
private val STRING_MAP: Map<String, String> = mapOf("a" to "b", "c" to "d")
private val STRING_QUEUE: Queue<String> = LinkedList(listOf("A", "b", "c"))
private val STRING_DEQUE: Deque<String> = java.util.ArrayDeque(listOf("A", "b", "c"))
private val STRING_PAIRS: Set<Pair<String, String>> = setOf("a" to "b", "c" to "d")
private val UUID_LIST: List<UUID> = (1..10).map { UUID.randomUUID() }
private val MAP_LIST: List<Map<Int, Int>> = listOf(mapOf(1 to 2, 3 to 4), mapOf(5 to 6, 7 to 8))
private val EXPECTED_MAP: Map<Int, Int> = mapOf(1 to 2, 3 to 4, 5 to 6, 7 to 8)
private val UNSPLIT_LIST: List<Int> = (1..20).toList()
private val SPLIT_LIST: List<List<Int>> =
    listOf((1..6).toList(), (7..12).toList(), (13..18).toList(), listOf(19, 20))
private val SPLIT_LIMITED_LIST: List<List<Int>> =
    listOf((1..6).toList(), (7..12).toList(), (13..20).toList())

class CollectionFunctionsTest {
    @Test
    fun `Contains ignore case`() {
        assertTrue(STRING_LIST.containsIgnoreCase("a"))
        assertTrue(STRING_LIST.containsIgnoreCase("A"))
        assertFalse(STRING_LIST.containsIgnoreCase("d"))
        assertTrue(STRING_MAP.containsIgnoreCase("a"))
        assertTrue(STRING_MAP.containsIgnoreCase("A"))
        assertFalse(STRING_MAP.containsIgnoreCase("d"))
    }

    @Test
    fun `Get ignore case`() {
        assertEquals("a", STRING_LIST.getIgnoreCase("a"))
        assertEquals("B", STRING_LIST.getIgnoreCase("b"))
        assertNull(STRING_LIST.getIgnoreCase("d"))
        assertEquals("b", STRING_MAP.getIgnoreCase("a"))
        assertEquals("d", STRING_MAP.getIgnoreCase("C"))
        assertNull(STRING_MAP.getIgnoreCase("d"))

        assertEquals("b", STRING_MAP["a", true])
        assertEquals("d", STRING_MAP["C", true])
        assertNull(STRING_MAP["C", false])
        assertNull(STRING_MAP["d", false])
    }

    @Test
    fun `Map removeIf`() {
        val map = STRING_MAP.toMutableMap()
        map.removeIf { k, v -> k == "a" && v == "b" }
        assertEquals(mapOf("c" to "d"), map)
    }

    @Test fun `Get random UUID`() = assertFalse(UUID_LIST.getRandomUUID() in UUID_LIST)

    @Test
    fun `Get key from value in map`() =
        assertEquals("a", STRING_MAP.getKey(STRING_MAP.getValue("a")))

    @Test fun `Flatten map list`() = assertEquals(EXPECTED_MAP, MAP_LIST.flatten())

    @Test
    fun `Immutable set`() {
        assertFailsWith<UnsupportedOperationException> {
            (STRING_SET.toImmutableSet() as MutableSet<String>).add("d")
        }
        assertFailsWith<UnsupportedOperationException> {
            (immutableSetOf("a") as MutableSet<String>).add("b")
        }
        assertFailsWith<UnsupportedOperationException> {
            (immutableSetOf(setOf("a")) as MutableSet<String>).add("b")
        }
    }

    @Test
    fun `Immutable list`() {
        assertFailsWith<UnsupportedOperationException> {
            (STRING_LIST.toImmutableList() as MutableList<String>).add("d")
        }
        assertFailsWith<UnsupportedOperationException> {
            (immutableListOf("a") as MutableList<String>).add("b")
        }
        assertFailsWith<UnsupportedOperationException> {
            (immutableListOf(listOf("a")) as MutableList<String>).add("b")
        }
    }

    @Test
    fun `Immutable map`() {
        assertFailsWith<UnsupportedOperationException> {
            (STRING_MAP.toImmutableMap() as MutableMap<String, String>)["e"] = "f"
        }
        assertFailsWith<UnsupportedOperationException> {
            (immutableMapOf("a" to "b") as MutableMap<String, String>)["c"] = "d"
        }
        assertFailsWith<UnsupportedOperationException> {
            (immutableMapOf(listOf("a" to "b")) as MutableMap<String, String>)["c"] = "d"
        }
        assertFailsWith<UnsupportedOperationException> {
            (listOf("a" to "b").toImmutableMap() as MutableMap<String, String>)["c"] = "d"
        }
    }

    @Test
    fun `Concurrent set`() = assertDoesNotThrow {
        val set = concurrentSetOf(1, 2, 3)
        for (i in set) {
            set.remove(i)
        }
    }

    @Test
    fun `Concurrent Deque`() = assertDoesNotThrow {
        val deque = listOf(1, 2, 3).toConcurrentDeque()
        for (i in deque) {
            deque.remove(i)
        }
    }

    @Test
    fun `Concurrent map`() = assertDoesNotThrow {
        val map = mapOf(1 to 2, 3 to 4, 5 to 6).toConcurrentMap()
        for (i in map) {
            map.remove(i.key)
        }
    }

    @Test
    fun `Split list`() {
        assertContentEquals(UNSPLIT_LIST.split(6), SPLIT_LIST)
        assertDoesNotThrow { UNSPLIT_LIST.split(21) }
        assertContentEquals(UNSPLIT_LIST.split(21), listOf(UNSPLIT_LIST))
    }

    @Test
    fun `Split list with limit`() =
        assertContentEquals(UNSPLIT_LIST.split(6, 3), SPLIT_LIMITED_LIST)

    @Test
    fun `Cast types of collections`() {
        val stringIterable: Iterable<*> = STRING_LIST
        assertFailsWith<ClassCastException> { stringIterable.castCheckedCollection<Int>() }
        assertDoesNotThrow { stringIterable.castCheckedCollection<String>().first() }
        assertNull(stringIterable.castCheckedCollectionSafe<Int>())
        assertSame(STRING_LIST, stringIterable.castCheckedCollectionSafe<String>())
        val stringList: List<*> = STRING_LIST
        assertFailsWith<ClassCastException> { stringList.castChecked<Int>() }
        assertDoesNotThrow { stringList.castChecked<String>().first() }
        assertNull(stringList.castCheckedSafe<Int>())
        assertSame(STRING_LIST, stringList.castCheckedSafe<String>())
        val stringSet: Set<*> = STRING_SET
        assertFailsWith<ClassCastException> { stringSet.castChecked<Int>() }
        assertDoesNotThrow { stringSet.castChecked<String>().first() }
        assertNull(stringSet.castCheckedSafe<Int>())
        assertSame(STRING_SET, stringSet.castCheckedSafe<String>())
        val stringMap: Map<*, *> = STRING_MAP
        assertFailsWith<ClassCastException> { stringMap.castChecked<Int, Int>() }
        assertDoesNotThrow { stringMap.castChecked<String, String>().entries.first() }
        assertNull(stringMap.castCheckedSafe<Int, Int>())
        assertSame(STRING_MAP, stringMap.castCheckedSafe<String, String>())
        val stringQueue: Queue<*> = STRING_QUEUE
        assertFailsWith<ClassCastException> { stringQueue.castChecked<Int>() }
        assertDoesNotThrow { stringQueue.castChecked<String>().first() }
        assertNull(stringQueue.castCheckedSafe<Int>())
        assertSame(STRING_QUEUE, stringQueue.castCheckedSafe())
        val stringDeque: Deque<*> = STRING_DEQUE
        assertFailsWith<ClassCastException> { stringDeque.castChecked<Int>() }
        assertDoesNotThrow { stringDeque.castChecked<String>().first() }
        assertNull(stringDeque.castCheckedSafe<Int>())
        assertSame(STRING_DEQUE, stringDeque.castCheckedSafe<String>())
        assertEquals(STRING_DEQUE.toList(), stringDeque.castCheckedSafe<String>()?.toList())
    }

    @Test
    fun `Map to pairs set`() {
        val map = STRING_MAP.toMutableMap()
        val pairs = map.pairsCopy()
        assertEquals(STRING_PAIRS, pairs)
        map.clear()
        assertTrue(pairs.isNotEmpty())
    }
}
