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

@file:Suppress("DEPRECATION")

package me.dkim19375.dkimcore.extension

import me.dkim19375.dkimcore.annotation.API
import org.apache.commons.lang3.text.StrSubstitutor
import org.apache.commons.text.StringSubstitutor
import java.util.*

private val usesLang3 = runCatchingOrNull { Class.forName("org.apache.commons.lang3.StringUtils") } != null
private val usesApacheText = runCatchingOrNull { Class.forName("org.apache.commons.text.WordUtils") } != null

@API
fun String.toUUID(): UUID? {
    try {
        return UUID.fromString(replace(" ", ""))
    } catch (ignored: IllegalArgumentException) {
    }
    val new = replace('-', ' ').replace(" ", "")
    try {
        val uuid1: String = new.substring(0, 8)
        val uuid2: String = new.substring(8, 12)
        val uuid3: String = new.substring(12, 16)
        val uuid4: String = new.substring(16, 20)
        val uuid5: String = new.substring(20)
        return UUID.fromString("$uuid1-$uuid2-$uuid3-$uuid4-$uuid5")
    } catch (_: IndexOutOfBoundsException) {
    } catch (_: IllegalArgumentException) {
    }
    return null
}

@API
fun String.setPlaceholders(map: Map<String, String>, wrapPercent: Boolean = true): String = if (wrapPercent) {
    setPlaceholders(map, "%", "%")
} else {
    setPlaceholders(map, "", "")
}

/**
 * Sets placeholders in a String
 *
 *  EXAMPLE:
 *
 *      "Text (placeholder) and *(escaped)".setPlaceholders(
 *          map = mapOf(
 *              "placeholder" to "replaced",
 *              "escaped" to "not replaced"
 *          ),
 *          prefix = "(",
 *          suffix = ")",
 *          escape = '*'
 *      )
 *
 *  will return "Text replaced and (escaped)"
 *
 * @param map The map to replace the text with - Key = old value, Value = new value
 * @param prefix The prefix of the placeholder to replace - can be blank
 * @param suffix The suffix of the placeholder to replace - can be blank
 * @param escape A character to exclude the placeholder from being parsed - '0' = don't escape
 * @return The String with its placeholders replaced
 */
@API
fun String.setPlaceholders(map: Map<String, String>, prefix: String, suffix: String, escape: Char = '0'): String {
    val slowest = {
        var new = this
        for ((text, replacement) in map) {
            new = new.replace(if (prefix.isEmpty() && suffix.isEmpty()) text else "$prefix$text$suffix", replacement)
        }
        new
    }
    return when {
        usesApacheText -> if (prefix.isEmpty() || suffix.isEmpty()) {
            slowest()
        } else {
            StringSubstitutor(map, prefix, suffix, escape).replace(this)
        }
        usesLang3 -> if (prefix.isEmpty() || suffix.isEmpty()) {
            slowest()
        } else {
            StrSubstitutor(map, prefix, suffix, escape).replace(this)
        }
        else -> slowest()
    }
}