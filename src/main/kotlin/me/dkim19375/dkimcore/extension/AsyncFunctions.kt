package me.dkim19375.dkimcore.extension

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