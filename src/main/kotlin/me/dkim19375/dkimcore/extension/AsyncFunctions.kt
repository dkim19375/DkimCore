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
import me.dkim19375.dkimcore.async.ActionConsumer
import me.dkim19375.dkimcore.async.ExecutorsConsumer
import java.util.concurrent.*

fun <T> Future<T>.getResult(timeout: Long? = null, unit: TimeUnit = TimeUnit.MILLISECONDS): Result<T> = runCatching {
    if (timeout != null) {
        get(timeout, unit)
    } else {
        get()
    }
}.let { oldResult ->
    val error = oldResult.exceptionOrNull()
    if (error != null) {
        if (error is ExecutionException) {
            val cause = error.cause
            if (cause != null) {
                return@let Result.failure<T>(cause)
            }
        }
        return@let Result.failure<T>(error)
    }
    Result.success(oldResult.getOrThrow())
}

@API
fun <T> runWithTimeout(
    timeout: Long,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    consumer: (task: () -> T) -> ActionConsumer<T> = { consumerTask -> ExecutorsConsumer(task = consumerTask) },
    task: () -> T
): T = consumer(task).completeWithTimeout(timeout, unit)

@API
fun <T> runWithSafeTimeout(
    timeout: Long,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    consumer: (task: () -> T) -> ActionConsumer<T> = { consumerTask -> ExecutorsConsumer(task = consumerTask) },
    task: () -> T
): T? = consumer(task).completeWithSafeTimeout(timeout, unit)

@API
suspend fun <T> awaitWithTimeout(
    timeout: Long,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    consumer: (task: () -> T) -> ActionConsumer<T> = { consumerTask -> ExecutorsConsumer(task = consumerTask) },
    task: () -> T
): T = consumer(task).awaitWithTimeout(timeout, unit)

@API
suspend fun <T> awaitWithSafeTimeout(
    timeout: Long,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    consumer: (task: () -> T) -> ActionConsumer<T> = { consumerTask -> ExecutorsConsumer(task = consumerTask) },
    task: () -> T
): T? = consumer(task).awaitWithSafeTimeout(timeout, unit)