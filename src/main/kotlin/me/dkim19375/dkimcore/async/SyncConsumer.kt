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

import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation

class SyncConsumer<T>(task: () -> T) : ActionConsumer<T>(task) {
    @Deprecated(
        message = "Will do the same as await(failure)",
        replaceWith = ReplaceWith("await(failure)"),
    )
    override suspend fun awaitWithTimeout(
        timeout: Long,
        unit: TimeUnit,
        failure: (Continuation<T>, Throwable) -> Unit,
    ): T = await(failure)

    @Deprecated(
        message = "Will do the same as await(failure)",
        replaceWith = ReplaceWith("await(failure)"),
    )
    override suspend fun awaitWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit,
        failure: (Continuation<T>, Throwable) -> Unit,
    ): T? = await(failure)

    @Deprecated(
        message = "Will do the same as queue(success, failure)",
        replaceWith = ReplaceWith("queue(success, failure)"),
    )
    override fun queueWithTimeout(
        timeout: Long,
        unit: TimeUnit,
        success: (T) -> Unit,
        failure: (Throwable) -> Unit,
    ) = queue(success, failure)

    @Deprecated(
        message = "Will do the same as queue(success, failure)",
        replaceWith = ReplaceWith("queue(success, failure)"),
    )
    override fun queueWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit,
        success: (T?) -> Unit,
        failure: (Throwable) -> Unit,
    ) = queue(success, failure)

    @Deprecated(message = "Will do the same as complete()", replaceWith = ReplaceWith("complete()"))
    override fun completeWithTimeout(timeout: Long, unit: TimeUnit): T = complete()

    @Deprecated(message = "Will do the same as complete()", replaceWith = ReplaceWith("complete()"))
    override fun completeWithSafeTimeout(timeout: Long, unit: TimeUnit): T? = complete()
}
