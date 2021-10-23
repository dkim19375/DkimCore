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

package me.dkim19375.dkimcore.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.dkim19375.dkimcore.annotation.API
import me.dkim19375.dkimcore.extension.SCOPE
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@API
class ActionConsumer<T>(private val scope: CoroutineScope? = SCOPE, val task: () -> T) {

    @API
    suspend fun await(
        failure: (Continuation<T>, Throwable) -> Unit = { continuation, throwable ->
            continuation.resumeWithException(throwable)
        },
    ): T = suspendCoroutine { cont ->
        this.queue({ cont.resume(it) }, { failure(cont, it) })
    }

    @API
    fun queue(success: ((T) -> Unit) = {}, failure: ((Throwable) -> Unit) = {
        throw it
    }) {
        val scope = scope
        if (scope == null) {
            try {
                success(task())
            } catch (error: Throwable) {
                failure(error)
                return
            }
            return
        }
        scope.launch {
            try {
                success(task())
            } catch (error: Throwable) {
                failure(error)
                return@launch
            }
        }
    }

    @API
    fun complete(): T = task()

    @API
    fun submit(): CompletableFuture<T> {
        if (scope == null) {
            return try {
                CompletableFuture.completedFuture(task())
            } catch (error: Throwable) {
                CompletableFuture.failedFuture(error)
            }
        }
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
}