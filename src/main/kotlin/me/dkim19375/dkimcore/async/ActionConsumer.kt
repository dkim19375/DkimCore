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

package me.dkim19375.dkimcore.async

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import me.dkim19375.dkimcore.annotation.API

@API
abstract class ActionConsumer<T>(open val task: () -> T) {

    @API
    open suspend fun await(
        failure: (Continuation<T>, Throwable) -> Unit = failure@{ continuation, throwable ->
            continuation.resumeWithException(throwable)
        }
    ): T = suspendCoroutine { cont -> queue({ cont.resume(it) }, { failure(cont, it) }) }

    @API
    abstract suspend fun awaitWithTimeout(
        timeout: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        failure: (Continuation<T>, Throwable) -> Unit = failure@{ continuation, throwable ->
            continuation.resumeWithException(throwable)
        },
    ): T

    @API
    abstract suspend fun awaitWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        failure: (Continuation<T>, Throwable) -> Unit = failure@{ continuation, throwable ->
            continuation.resumeWithException(throwable)
        },
    ): T?

    @API
    open fun queue(success: ((T) -> Unit) = {}, failure: ((Throwable) -> Unit) = { throw it }) {
        try {
            success(task())
        } catch (error: Throwable) {
            failure(error)
            return
        }
    }

    @API
    abstract fun queueWithTimeout(
        timeout: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        success: ((T) -> Unit) = {},
        failure: ((Throwable) -> Unit) = { throw it },
    )

    @API
    abstract fun queueWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        success: ((T?) -> Unit) = {},
        failure: ((Throwable) -> Unit) = { throw it },
    )

    @API open fun complete(): T = task()

    @API abstract fun completeWithTimeout(timeout: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): T

    @API
    abstract fun completeWithSafeTimeout(timeout: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): T?

    @API
    open fun submit(): Future<T> =
        try {
            CompletableFuture.completedFuture(task())
        } catch (error: Throwable) {
            CompletableFuture.failedFuture(error)
        }
}
