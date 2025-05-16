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
import me.dkim19375.dkimcore.annotation.API

class ExecutorsConsumer<T>(
    private val executor: ExecutorService = Executors.newCachedThreadPool(),
    task: () -> T,
) : ActionConsumer<T>(task) {

    override suspend fun awaitWithTimeout(
        timeout: Long,
        unit: TimeUnit,
        failure: (Continuation<T>, Throwable) -> Unit,
    ): T = suspendCoroutine { cont ->
        executor.submit {
            try {
                cont.resume(FutureTask(task).get(timeout, unit))
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
        executor.submit {
            try {
                cont.resume(FutureTask(task).get(timeout, unit))
            } catch (error: Throwable) {
                if (error is TimeoutException) {
                    cont.resume(null)
                    return@submit
                }
                cont.resumeWithException(error)
            }
        }
    }

    @API
    override fun queue(success: ((T) -> Unit), failure: ((Throwable) -> Unit)) {
        executor.submit { super.queue(success, failure) }
    }

    override fun queueWithTimeout(
        timeout: Long,
        unit: TimeUnit,
        success: (T) -> Unit,
        failure: (Throwable) -> Unit,
    ) {
        val future = FutureTask(task)
        executor.submit(future::run)
        executor.submit {
            try {
                success(future.get(timeout, unit))
            } catch (error: Throwable) {
                failure(error)
            }
        }
    }

    override fun queueWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit,
        success: (T?) -> Unit,
        failure: (Throwable) -> Unit,
    ) {
        val future = FutureTask(task)
        executor.submit(future::run)
        executor.submit {
            try {
                success(future.get(timeout, unit))
            } catch (error: Throwable) {
                if (error is TimeoutException) {
                    success(null)
                    return@submit
                }
                failure(error)
            }
        }
    }

    @API
    override fun submit(): Future<T> {
        val future = CompletableFuture<T>()
        executor.submit future@{
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
        return submit().get(timeout, unit)
    }

    override fun completeWithSafeTimeout(timeout: Long, unit: TimeUnit): T? {
        return try {
            submit().get(timeout, unit)
        } catch (_: TimeoutException) {
            null
        }
    }
}
