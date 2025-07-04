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

data class TimeStringFormat(val short: String, val singular: String, val plural: String) {
    fun format(count: Long, useShort: Boolean, requireNonBlankResult: Boolean = false): String =
        when {
            (count >= 1 || requireNonBlankResult) && useShort -> "${count}$short"
            count == 1L -> "$count$singular"
            count > 1 || requireNonBlankResult -> "$count$plural"
            else -> ""
        }
}

private val defaultTimeStringFormatDays =
    TimeStringFormat(short = "d ", singular = " Day, ", plural = " Days, ")
private val defaultTimeStringFormatHours =
    TimeStringFormat(short = "h ", singular = " Hour, ", plural = " Hours, ")
private val defaultTimeStringFormatMinutes =
    TimeStringFormat(short = "m ", singular = " Minute, and ", plural = " Minutes, and ")
private val defaultTimeStringFormatSeconds =
    TimeStringFormat(short = "s", singular = " Second", plural = " Seconds")

fun Long.getTimeFormatted(
    short: Boolean = false,
    secondsFormat: TimeStringFormat = defaultTimeStringFormatSeconds,
    minutesFormat: TimeStringFormat = defaultTimeStringFormatMinutes,
    hoursFormat: TimeStringFormat = defaultTimeStringFormatHours,
    daysFormat: TimeStringFormat = defaultTimeStringFormatDays,
): String {
    var seconds = this
    val days = seconds / 86400
    seconds %= 86400
    val hours = seconds / 3600
    seconds %= 3600
    val minutes = seconds / 60
    seconds %= 60
    val daysStr = daysFormat.format(days, short)
    val hoursStr = hoursFormat.format(hours, short, daysStr.isNotBlank())
    val minutesStr = minutesFormat.format(minutes, short, hoursStr.isNotBlank())
    val secondsStr = secondsFormat.format(seconds, short, true)
    return "$daysStr$hoursStr$minutesStr$secondsStr"
}
