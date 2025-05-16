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

class LazyReadOnlyMap<K, V>(private val internalMap: Map<K, Lazy<V>>) : AbstractMap<K, V>() {

    override val entries: Set<Map.Entry<K, V>> =
        object : AbstractSet<Map.Entry<K, V>>() {

            override val size: Int = internalMap.size

            override fun iterator(): Iterator<Map.Entry<K, V>> =
                object : Iterator<Map.Entry<K, V>> {
                    private val internalIterator = internalMap.iterator()

                    override fun hasNext(): Boolean = internalIterator.hasNext()

                    override fun next(): Map.Entry<K, V> =
                        object : Map.Entry<K, V> {
                            private val entry = internalIterator.next()

                            override val key: K = entry.key
                            override val value: V by entry.value
                        }
                }
        }

    override val keys: Set<K> = internalMap.keys

    override fun containsKey(key: K): Boolean = internalMap.containsKey(key)
}
