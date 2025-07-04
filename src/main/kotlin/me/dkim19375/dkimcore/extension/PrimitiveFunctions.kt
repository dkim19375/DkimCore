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

import java.util.TreeMap
import kotlin.math.pow
import kotlin.math.roundToLong
import me.dkim19375.dkimcore.annotation.API

fun Float.setDecimalPlaces(amount: Int?): Float {
    amount?.takeIf { amount < Int.MAX_VALUE } ?: return this
    return 10f.pow(amount).let { pow -> (this * pow).roundToLong() / pow }
}

fun Double.setDecimalPlaces(amount: Int?): Double {
    amount?.takeIf { amount < Int.MAX_VALUE } ?: return this
    return 10.0.pow(amount).let { pow -> (this * pow).roundToLong() / pow }
}

@API fun Long.getPercentage(max: Long): Long = this * 100 / max

@API fun Double.getPercentage(max: Double): Double = this * 100 / max

@API fun Number.percentChance(): Boolean = Math.random() * 100 <= toDouble()

private val romanNumeralMap =
    TreeMap<Int, String>().apply {
        put(1, "I")
        put(4, "IV")
        put(5, "V")
        put(9, "IX")
        put(10, "X")
        put(40, "XL")
        put(50, "L")
        put(90, "XC")
        put(100, "C")
        put(400, "CD")
        put(500, "D")
        put(900, "CM")
        put(1000, "M")
    }

fun Int.toRomanNumeral(limit: Int = 3999): String {
    if (this > limit || this > 3999) {
        return toString()
    }
    val num = romanNumeralMap.floorKey(this)
    if (this == num) {
        return romanNumeralMap[num] ?: "0"
    }
    return romanNumeralMap[num] + (this - num).toRomanNumeral()
}

fun Double.removeRedundantDecimal(): Number = if (this % 1.0 == 0.0) this.toLong() else this

private val suffixes =
    listOf(1_000_000_000_000L to 'T', 1_000_000_000L to 'B', 1_000_000L to 'M', 1_000L to 'K')

fun Number.formatWithSuffix(decimalPlaces: Int = Int.MAX_VALUE): String {
    val double = toDouble()
    for ((value, suffix) in suffixes) {
        if (double < value) {
            continue
        }
        return "${
            (double / value).setDecimalPlaces(decimalPlaces).removeRedundantDecimal()
        }$suffix"
    }
    return double.setDecimalPlaces(decimalPlaces).removeRedundantDecimal().toString()
}
