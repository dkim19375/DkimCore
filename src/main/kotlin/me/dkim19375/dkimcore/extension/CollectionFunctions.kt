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
import java.util.*

@API
fun Iterable<String>.containsIgnoreCase(find: String): Boolean = getIgnoreCase(find) != null

@API
fun Iterable<String>.getIgnoreCase(find: String): String? = firstOrNull { it.equals(find, ignoreCase = true) }

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
fun <T> Collection<Collection<T>>.combine(): List<T> {
    return fold(mutableListOf()) { list: MutableList<T>, collection: Collection<T> ->
        list.addAll(collection)
        return@fold list
    }
}