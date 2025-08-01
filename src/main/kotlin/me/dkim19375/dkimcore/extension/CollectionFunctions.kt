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

import java.util.Collections
import java.util.Deque
import java.util.Queue
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.jvm.jvmName
import me.dkim19375.dkimcore.annotation.API

@API fun Iterable<String>.containsIgnoreCase(find: String): Boolean = getIgnoreCase(find) != null

@API fun Map<String, *>.containsIgnoreCase(find: String): Boolean = getIgnoreCase(find) != null

@API
fun Iterable<String>.getIgnoreCase(find: String): String? = firstOrNull {
    it.equals(find, ignoreCase = true)
}

@API fun <V> Map<String, V>.getIgnoreCase(find: String): V? = this[find, true]

@API
operator fun <V> Map<String, V>.get(other: String, ignoreCase: Boolean = false): V? =
    entries.firstOrNull { it.key.equals(other, ignoreCase = ignoreCase) }?.value

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

@API fun <K, V> Map<K, V>.getKey(value: V): K? = entries.firstOrNull { it.value == value }?.key

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

@API fun <T> immutableSetOf(vararg elements: T): Set<T> = setOf(*elements).toImmutableSet()

@API fun <T> immutableSetOf(elements: Collection<T>): Set<T> = elements.toImmutableSet()

fun <T> Iterable<T>.toImmutableList(): List<T> = Collections.unmodifiableList(toList())

@API fun <T> immutableListOf(vararg elements: T): List<T> = listOf(*elements).toImmutableList()

@API fun <T> immutableListOf(elements: Collection<T>): List<T> = elements.toImmutableList()

@API fun <K, V> Iterable<Pair<K, V>>.toImmutableMap(): Map<K, V> = toMap().toImmutableMap()

fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> = Collections.unmodifiableMap(this)

@API
fun <K, V> immutableMapOf(vararg elements: Pair<K, V>): Map<K, V> =
    mapOf(*elements).toImmutableMap()

@API
fun <K, V> immutableMapOf(elements: Collection<Pair<K, V>>): Map<K, V> =
    elements.toMap().toImmutableMap()

fun <T> Iterable<T>.toConcurrentSet(): MutableSet<T> =
    Collections.newSetFromMap<T>(ConcurrentHashMap()).also { set -> set.addAll(this) }

@API fun <T> concurrentSetOf(vararg elements: T): MutableSet<T> = setOf(*elements).toConcurrentSet()

@API fun <T> concurrentSetOf(elements: Collection<T>): MutableSet<T> = elements.toConcurrentSet()

fun <T> Iterable<T>.toConcurrentDeque(): Deque<T> =
    ConcurrentLinkedDeque((this as? Collection<T>) ?: toList())

@API fun <T> concurrentDequeOf(vararg elements: T): Deque<T> = listOf(*elements).toConcurrentDeque()

@API fun <T> concurrentDequeOf(elements: Collection<T>): Deque<T> = elements.toConcurrentDeque()

@API fun <K, V> Iterable<Pair<K, V>>.toConcurrentMap(): MutableMap<K, V> = toMap().toConcurrentMap()

fun <K, V> Map<K, V>.toConcurrentMap(): MutableMap<K, V> = ConcurrentHashMap(this)

@API
fun <K, V> concurrentMapOf(vararg elements: Pair<K, V>): MutableMap<K, V> =
    mapOf(*elements).toConcurrentMap()

@API
fun <K, V> concurrentMapOf(elements: Collection<Pair<K, V>>): MutableMap<K, V> =
    elements.toMap().toConcurrentMap()

inline fun <reified T, C : Iterable<T>> Iterable<*>.castCheckedTo(): C {
    for ((i, item) in withIndex()) {
        if (item is T) {
            continue
        }
        throw ClassCastException(
            "${item?.let { it::class.qualifiedName ?: it::class.jvmName }} at index $i in " +
                "${this::class.qualifiedName ?: this::class.jvmName} cannot be cast to " +
                (T::class.qualifiedName ?: T::class.jvmName)
        )
    }
    @Suppress("UNCHECKED_CAST") // checked
    return this as C
}

inline fun <reified T, C : Iterable<T>> Iterable<*>.castCheckedSafelyTo(): C? {
    for (item in this) {
        if (item !is T) {
            return null
        }
    }
    @Suppress("UNCHECKED_CAST") // checked
    return this as C
}

@API inline fun <reified T> Iterable<*>.castCheckedCollection(): Collection<T> = castCheckedTo()

@API
inline fun <reified T> Iterable<*>.castCheckedCollectionSafe(): Collection<T>? =
    castCheckedSafelyTo<T, Collection<T>>()

@API inline fun <reified T> List<*>.castChecked(): List<T> = castCheckedTo()

@API inline fun <reified T> List<*>.castCheckedSafe(): List<T>? = castCheckedSafelyTo<T, List<T>>()

@API inline fun <reified T> Set<*>.castChecked(): Set<T> = castCheckedTo()

@API inline fun <reified T> Set<*>.castCheckedSafe(): Set<T>? = castCheckedSafelyTo<T, Set<T>>()

inline fun <reified K, reified V, M : Map<K, V>> Map<*, *>.castCheckedTo(): M {
    val keyName = K::class.qualifiedName ?: K::class.jvmName
    val valueName = V::class.qualifiedName ?: V::class.jvmName
    val thisName = this::class.qualifiedName ?: this::class.jvmName
    for ((key, value) in this) {
        if (key is K) {
            if (value is V) {
                continue
            }
            throw ClassCastException(
                "${value?.let { it::class.qualifiedName ?: it::class.jvmName }} in " +
                    "$thisName cannot be cast to $valueName"
            )
        }
        throw ClassCastException(
            "${key?.let { it::class.qualifiedName ?: it::class.jvmName }} in " +
                "$thisName cannot be cast to $keyName"
        )
    }
    @Suppress("UNCHECKED_CAST") // checked
    return this as M
}

inline fun <reified K, reified V, M : Map<K, V>> Map<*, *>.castCheckedSafelyTo(): M? {
    for ((key, value) in this) {
        if (key !is K || value !is V) {
            return null
        }
    }
    @Suppress("UNCHECKED_CAST") // checked
    return this as M
}

@API inline fun <reified K, reified V> Map<*, *>.castChecked(): Map<K, V> = castCheckedTo()

@API
inline fun <reified K, reified V> Map<*, *>.castCheckedSafe(): Map<K, V>? =
    castCheckedSafelyTo<K, V, Map<K, V>>()

@API inline fun <reified T> Queue<*>.castChecked(): Queue<T> = castCheckedTo()

@API
inline fun <reified T> Queue<*>.castCheckedSafe(): Queue<T>? = castCheckedSafelyTo<T, Queue<T>>()

@API inline fun <reified T> Deque<*>.castChecked(): Deque<T> = castCheckedTo()

@API
inline fun <reified T> Deque<*>.castCheckedSafe(): Deque<T>? = castCheckedSafelyTo<T, Deque<T>>()

fun <K, V> Map<K, V>.pairsCopy(): Set<Pair<K, V>> =
    buildSet(size) {
        for ((k, v) in this@pairsCopy) {
            add(k to v)
        }
    }
