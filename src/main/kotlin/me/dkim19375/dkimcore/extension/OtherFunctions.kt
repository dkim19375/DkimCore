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

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.isAccessible
import me.dkim19375.dkimcore.annotation.API

@API inline fun <reified T> typedNull(): T? = null

fun <T> runCatchingOrNull(action: () -> T?): T? =
    try {
        action()
    } catch (e: Throwable) {
        null
    }

@API
fun <T> getMillisAndValue(function: () -> T): Pair<Double, T> {
    val start = System.nanoTime()
    val value = function()
    val end = System.nanoTime()
    return ((end - start).toDouble() / 1000000.0) to value
}

@API
fun <T> getMillisLongAndValue(function: () -> T): Pair<Long, T> {
    val start = System.currentTimeMillis()
    val value = function()
    val end = System.currentTimeMillis()
    return (end - start) to value
}

@API
inline fun <reified T : Enum<T>> enumValueOfOrNull(str: String): T? = runCatchingOrNull {
    enumValueOf<T>(str.uppercase())
}

fun <T : Any> KClass<T>.createInstance(): T {
    val noArgsConstructor =
        constructors
            .filter { it.parameters.all(KParameter::isOptional) }
            .ifEmpty {
                throw IllegalStateException("Class should have a single no-arg constructor: $this")
            }

    noArgsConstructor.singleOrNull(KFunction<T>::isAccessible)?.let {
        return it.callBy(emptyMap())
    }

    return noArgsConstructor.single().apply { isAccessible = true }.callBy(emptyMap())
}
