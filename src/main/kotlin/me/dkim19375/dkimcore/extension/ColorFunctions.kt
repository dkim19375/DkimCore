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

private val colors =
    Color::class
        .java
        .fields
        .filter { field -> field.name.uppercase() == field.name }
        .mapNotNull { field -> (field.get(null) as? Color)?.let { field.name to it } }
        .toImmutableMap()

private val rgbRegex = "(\\d{1,3})(?: *, *| +)*(\\d{1,3})(?: *, *| +)*(\\d{1,3})".toRegex()

fun String.getColor(): Color? {
    val colorMap = getColorsNameMap()[this, true]
    if (colorMap != null) {
        return colorMap
    }
    runCatching { Color.decode(this) }
        .getOrNull()
        ?.let {
            return it
        }
    runCatching { Color.decode("#$this") }
        .getOrNull()
        ?.let {
            return it
        }
    val rgbColorStr = removePrefix("rgb").removePrefix("(").removeSuffix(")")
    val match = rgbRegex.find(rgbColorStr) ?: return null
    val (r, g, b) = match.destructured
    return runCatching { Color(r.toInt(), g.toInt(), b.toInt()) }.getOrNull()
}

fun getColorsNameMap(): Map<String, Color> = colors
