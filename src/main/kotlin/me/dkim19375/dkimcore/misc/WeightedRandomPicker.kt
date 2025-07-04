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

import kotlin.random.Random
import me.dkim19375.dkimcore.extension.pairsCopy

class WeightedRandomPicker<T>(items: Collection<Pair<T, Number>>) {
    private val items =
        items.map { it.first to it.second.toDouble() }.filter { it.second > 0.0 }.toMutableList()
    private var total = this.items.sumOf(Pair<T, Double>::second)

    constructor(items: Map<T, Number>) : this(items.pairsCopy())

    /**
     * Pick a random item from the list of items, weighted by their weights.
     *
     * @param removeResult If true, the picked item will be removed from the list of available items
     * @return the picked item
     * @throws NoItemsException if there are no items to pick from
     */
    fun pickItem(removeResult: Boolean = false): T {
        if (items.isEmpty()) {
            throw NoItemsException()
        }
        if (items.size == 1) {
            val lastItem = items.first().first
            if (removeResult) {
                items.clear()
                total = 0.0
            }
            return lastItem
        }
        val randomNum = Random.nextDouble() * total
        var current = 0.0
        for ((i, item) in items.withIndex()) {
            current += item.second
            if (current > randomNum) {
                if (removeResult) {
                    items.removeAt(i)
                    total -= item.second
                }
                return item.first
            }
        }
        throw IllegalStateException("No item found")
    }

    fun getRemainingItems(): Set<Pair<T, Double>> = items.toSet()

    class NoItemsException : RuntimeException("No items to choose from")
}
