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

import java.util.concurrent.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.dkim19375.dkimcore.annotation.API
import me.dkim19375.dkimcore.extension.*

@Suppress("BlockingMethodInNonBlockingContext")
@API
open class CoroutineConsumer<T>(protected open val scope: CoroutineScope = SCOPE, task: () -> T) :
    ActionConsumer<T>(task) {

    override suspend fun awaitWithTimeout(
        timeout: Long,
        unit: TimeUnit,
        failure: (Continuation<T>, Throwable) -> Unit,
    ): T = suspendCoroutine { cont ->
        scope.launch {
            try {
                cont.resume(FutureTask(task).getResult(timeout, unit).getOrThrow())
            } catch (error: Throwable) {
                cont.resumeWithException(error)
            }
        }
    }

    override suspend fun awaitWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit,
        failure: (Continuation<T>, Throwable) -> Unit,
    ): T? = suspendCoroutine { cont ->
        scope.launch {
            try {
                cont.resume(FutureTask(task).getResult(timeout, unit).getOrThrow())
            } catch (error: Throwable) {
                if (error is TimeoutException) {
                    cont.resume(null)
                    return@launch
                }
                cont.resumeWithException(error)
            }
        }
    }

    @API
    override fun queue(success: ((T) -> Unit), failure: ((Throwable) -> Unit)) {
        scope.launch { super.queue(success, failure) }
    }

    override fun queueWithTimeout(
        timeout: Long,
        unit: TimeUnit,
        success: (T) -> Unit,
        failure: (Throwable) -> Unit,
    ) {
        scope.launch {
            val future = FutureTask(task)
            launch { future.run() }
            launch {
                try {
                    success(future.getResult(timeout, unit).getOrThrow())
                } catch (error: Throwable) {
                    failure(error)
                }
            }
        }
    }

    override fun queueWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit,
        success: (T?) -> Unit,
        failure: (Throwable) -> Unit,
    ) {
        scope.launch {
            val future = FutureTask(task)
            launch { future.run() }
            launch task@{
                try {
                    success(future.getResult(timeout, unit).getOrThrow())
                } catch (error: Throwable) {
                    if (error is TimeoutException) {
                        success(null)
                        return@task
                    }
                    failure(error)
                }
            }
        }
    }

    @API
    override fun submit(): Future<T> {
        val future = CompletableFuture<T>()
        scope.launch future@{
            try {
                future.complete(task())
            } catch (error: Throwable) {
                future.completeExceptionally(error)
                return@future
            }
        }
        return future
    }

    override fun completeWithTimeout(timeout: Long, unit: TimeUnit): T {
        return submit().getResult(timeout, unit).getOrThrow()
    }

    override fun completeWithSafeTimeout(timeout: Long, unit: TimeUnit): T? {
        return try {
            submit().getResult(timeout, unit).getOrThrow()
        } catch (_: TimeoutException) {
            null
        }
    }
}
