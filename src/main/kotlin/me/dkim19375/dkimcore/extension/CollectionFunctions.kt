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

import me.dkim19375.dkimcore.annotation.API
import java.util.Collections
import java.util.Deque
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

@API
fun Iterable<String>.containsIgnoreCase(find: String): Boolean = getIgnoreCase(find) != null

@API
fun Map<String, *>.containsIgnoreCase(find: String): Boolean = getIgnoreCase(find) != null

@API
fun Iterable<String>.getIgnoreCase(find: String): String? = firstOrNull { it.equals(find, ignoreCase = true) }

@API
fun <V> Map<String, V>.getIgnoreCase(find: String): V? = this[find, true]

@API
operator fun <V> Map<String, V>.get(other: String, ignoreCase: Boolean = false): V? = entries.firstOrNull {
    it.key.equals(other, ignoreCase = ignoreCase)
}?.value

@API
fun <K, V> MutableMap<K, V>.removeIf(filter: (K, V) -> Boolean) {
    for ((k, v) in toMap()) {
        if (filter(k, v)) {
            remove(k)
        }
    }
}

@API
fun Collection<UUID>.getRandomUUID(): UUID {
    while (true) {
        val uuid = UUID.randomUUID()
        if (contains(uuid)) {
            continue
        }
        return uuid
    }
}

@API
fun <K, V> Map<K, V>.getKey(value: V): K? = entries.firstOrNull { it.value == value }?.key

@API
fun <K, V> Collection<Map<K, V>>.flatten(): Map<K, V> =
    fold(mutableMapOf()) { map: MutableMap<K, V>, collection: Map<K, V> ->
        map.putAll(collection)
        return@fold map
    }

@API
fun <T> Collection<T>.split(size: Int, limit: Int? = null): List<List<T>> =
    fold<T, MutableList<MutableList<T>>>(mutableListOf(mutableListOf())) { list, value ->
        val inner = list.lastOrNull() ?: return@fold mutableListOf(mutableListOf(value))
        if ((limit != null && limit <= list.size) || (inner.size < size)) {
            inner.add(value)
            return@fold list
        }
        val new = mutableListOf(value)
        list.add(new)
        list
    }

fun <T> Iterable<T>.toImmutableSet(): Set<T> = Collections.unmodifiableSet(toSet())

@API
fun <T> immutableSetOf(vararg elements: T): Set<T> = setOf(*elements).toImmutableSet()

@API
fun <T> immutableSetOf(elements: Collection<T>): Set<T> = elements.toImmutableSet()

fun <T> Iterable<T>.toImmutableList(): List<T> = Collections.unmodifiableList(toList())

@API
fun <T> immutableListOf(vararg elements: T): List<T> = listOf(*elements).toImmutableList()

@API
fun <T> immutableListOf(elements: Collection<T>): List<T> = elements.toImmutableList()

@API
fun <K, V> Iterable<Pair<K, V>>.toImmutableMap(): Map<K, V> = toMap().toImmutableMap()

fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> = Collections.unmodifiableMap(this)

@API
fun <K, V> immutableMapOf(vararg elements: Pair<K, V>): Map<K, V> = mapOf(*elements).toImmutableMap()

@API
fun <K, V> immutableMapOf(elements: Collection<Pair<K, V>>): Map<K, V> = elements.toMap().toImmutableMap()


fun <T> Iterable<T>.toConcurrentSet(): MutableSet<T> = Collections.newSetFromMap<T>(ConcurrentHashMap()).also { set ->
    set.addAll(this)
}

@API
fun <T> concurrentSetOf(vararg elements: T): MutableSet<T> = setOf(*elements).toConcurrentSet()

@API
fun <T> concurrentSetOf(elements: Collection<T>): MutableSet<T> = elements.toConcurrentSet()

fun <T> Iterable<T>.toConcurrentDeque(): Deque<T> = ConcurrentLinkedDeque((this as? Collection<T>) ?: toList())

@API
fun <T> concurrentDequeOf(vararg elements: T): Deque<T> = listOf(*elements).toConcurrentDeque()

@API
fun <T> concurrentDequeOf(elements: Collection<T>): Deque<T> = elements.toConcurrentDeque()

@API
fun <K, V> Iterable<Pair<K, V>>.toConcurrentMap(): MutableMap<K, V> = toMap().toConcurrentMap()

fun <K, V> Map<K, V>.toConcurrentMap(): MutableMap<K, V> = ConcurrentHashMap(this)

@API
fun <K, V> concurrentMapOf(vararg elements: Pair<K, V>): MutableMap<K, V> = mapOf(*elements).toConcurrentMap()

@API
fun <K, V> concurrentMapOf(elements: Collection<Pair<K, V>>): MutableMap<K, V> = elements.toMap().toConcurrentMap()

private fun <T, S : MutableCollection<T>> Iterable<*>.castType(
    castTo: Class<T>,
    name: String,
    collectionInit: () -> S,
): S {
    if (this is Collection<*> && isEmpty()) {
        return collectionInit()
    }
    val result = collectionInit()
    for (item in this) {
        if (castTo.isInstance(item)) {
            result.add(castTo.cast(item))
            continue
        }
        throw ClassCastException("Item ${item?.javaClass?.name} in $name cannot be cast to ${castTo.name}")
    }
    return result
}

private fun <T, S : MutableCollection<T>> Iterable<*>.castTypeSafe(
    castTo: Class<T>,
    collectionInit: () -> S,
): S? {
    if (this is Collection<*> && isEmpty()) {
        return collectionInit()
    }
    val result = collectionInit()
    for (item in this) {
        if (castTo.isInstance(item)) {
            result.add(castTo.cast(item))
            continue
        }
        return null
    }
    return result
}

@API
fun <T> Iterable<*>.castTypeCollection(castTo: Class<T>): Collection<T> = castType(castTo, "Iterable", ::arrayListOf)

@API
fun <T> Iterable<*>.castTypeCollectionSafe(castTo: Class<T>): Collection<T>? = castTypeSafe(castTo, ::arrayListOf)

@API
fun <T> List<*>.castType(castTo: Class<T>): List<T> = castType(castTo, "List", ::arrayListOf)

@API
fun <T> List<*>.castTypeSafe(castTo: Class<T>): List<T>? = castTypeSafe(castTo, ::arrayListOf)

@API
fun <T> Set<*>.castType(castTo: Class<T>): Set<T> = castType(castTo, "Set", ::hashSetOf)

@API
fun <T> Set<*>.castTypeSafe(castTo: Class<T>): Set<T>? = castTypeSafe(castTo, ::hashSetOf)

@API
fun <K, V> Map<*, *>.castType(castKeyTo: Class<K>, castValueTo: Class<V>): Map<K, V> {
    if (isEmpty()) {
        return hashMapOf()
    }
    val result = hashMapOf<K, V>()
    for ((key, value) in this) {
        if (!castKeyTo.isInstance(key)) {
            throw ClassCastException("Item (key) ${key?.javaClass?.name} in Map cannot be cast to ${castKeyTo.name}")
        }
        if (!castValueTo.isInstance(value)) {
            throw ClassCastException("Item (value) ${value?.javaClass?.name} in Map cannot be cast to ${castValueTo.name}")
        }
        result[castKeyTo.cast(key)] = castValueTo.cast(value)
    }
    return result
}

@API
fun <K, V> Map<*, *>.castTypeSafe(castKeyTo: Class<K>, castValueTo: Class<V>): Map<K, V>? {
    if (isEmpty()) {
        return hashMapOf()
    }
    val result = hashMapOf<K, V>()
    for ((key, value) in this) {
        if (!castKeyTo.isInstance(key)) {
            return null
        }
        if (!castValueTo.isInstance(value)) {
            return null
        }
        result[castKeyTo.cast(key)] = castValueTo.cast(value)
    }
    return result
}

@API
fun <T> Queue<*>.castType(castTo: Class<T>): Queue<T> = castType(castTo, "Queue", ::LinkedList)

@API
fun <T> Queue<*>.castTypeSafe(castTo: Class<T>): Queue<T>? = castTypeSafe(castTo, ::LinkedList)

@API
fun <T> Deque<*>.castType(castTo: Class<T>): Deque<T> = castType(castTo, "Deque") { java.util.ArrayDeque() }

@API
fun <T> Deque<*>.castTypeSafe(castTo: Class<T>): Deque<T>? = castTypeSafe(castTo) { java.util.ArrayDeque() }