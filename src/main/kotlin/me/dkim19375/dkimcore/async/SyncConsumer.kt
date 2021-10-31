package me.dkim19375.dkimcore.async

import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation

class SyncConsumer<T>(task: () -> T) : ActionConsumer<T>(task) {
    @Deprecated(
        message = "Will do the same as await(failure)",
        replaceWith = ReplaceWith("await(failure)")
    )
    override suspend fun awaitWithTimeout(
        timeout: Long,
        unit: TimeUnit,
        failure: (Continuation<T>, Throwable) -> Unit,
    ): T = await(failure)

    @Deprecated(
        message = "Will do the same as await(failure)",
        replaceWith = ReplaceWith("await(failure)")
    )
    override suspend fun awaitWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit,
        failure: (Continuation<T>, Throwable) -> Unit,
    ): T? = await(failure)

    @Deprecated(
        message = "Will do the same as queue(success, failure)",
        replaceWith = ReplaceWith("queue(success, failure)")
    )
    override fun queueWithTimeout(timeout: Long, unit: TimeUnit, success: (T) -> Unit, failure: (Throwable) -> Unit) =
        queue(success, failure)

    @Deprecated(
        message = "Will do the same as queue(success, failure)",
        replaceWith = ReplaceWith("queue(success, failure)")
    )
    override fun queueWithSafeTimeout(
        timeout: Long,
        unit: TimeUnit,
        success: (T?) -> Unit,
        failure: (Throwable) -> Unit,
    ) = queue(success, failure)

    @Deprecated(
        message = "Will do the same as complete()",
        replaceWith = ReplaceWith("complete()")
    )
    override fun completeWithTimeout(timeout: Long, unit: TimeUnit): T = complete()

    @Deprecated(
        message = "Will do the same as complete()",
        replaceWith = ReplaceWith("complete()")
    )
    override fun completeWithSafeTimeout(timeout: Long, unit: TimeUnit): T? = complete()
}