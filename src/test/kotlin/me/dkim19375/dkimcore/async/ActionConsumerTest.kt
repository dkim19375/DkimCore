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

package me.dkim19375.dkimcore.async

import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

internal class ActionConsumerTest {
    private fun <T> getConsumers(task: () -> T): Set<ActionConsumer<T>> = setOf(
        CoroutineConsumer { task() },
        ExecutorsConsumer { task() }
    )

    @Test
    fun `Test time of queue`() {
        val max = 500L
        getConsumers {
            Thread.sleep(300L)
        }.forEach { consumer ->
            val time = measureTimeMillis {
                val completed = CompletableFuture<Unit?>()
                consumer.queue({ completed.complete(null) }) { completed.completeExceptionally(it) }
                completed.get()
            }
            assertTrue(max > time,
                "Expected max to be greater than time: ${time}ms (type: ${consumer::class.simpleName})")
        }
    }

    @Test
    fun `Test timeout of queue`() {
        val max = 500L
        getConsumers {
            Thread.sleep(600L)
        }.forEach { consumer ->
            val time = measureTimeMillis {
                val completed = CompletableFuture<Unit?>()
                consumer.queueWithSafeTimeout(300L, success = { completed.complete(null) }) {
                    completed.completeExceptionally(it)
                }
                completed.get()
            }
            assertTrue(max > time,
                "Expected max to be greater than time: ${time}ms (type: ${consumer::class.simpleName})")
        }
    }

    @Test
    fun `Test time of complete`() {
        val max = 500L
        getConsumers {
            Thread.sleep(300L)
        }.forEach { consumer ->
            val time = measureTimeMillis {
                consumer.complete()
            }
            assertTrue(max > time,
                "Expected max to be greater than time: ${time}ms (type: ${consumer::class.simpleName})")
        }
    }

    @Test
    fun `Test timeout of complete`() {
        val max = 500L
        getConsumers {
            Thread.sleep(600L)
        }.forEach { consumer ->
            val time = measureTimeMillis {
                consumer.completeWithSafeTimeout(300L)
            }
            assertTrue(max > time,
                "Expected max to be greater than time: ${time}ms (type: ${consumer::class.simpleName})")
        }
    }

    @Test
    fun `Test time of submit`() {
        val max = 500L
        getConsumers {
            Thread.sleep(300L)
        }.forEach { consumer ->
            val time = measureTimeMillis {
                consumer.submit().get()
            }
            assertTrue(max > time,
                "Expected max to be greater than time: ${time}ms (type: ${consumer::class.simpleName})")
        }
    }

    @Test
    fun `Test time of await`() {
        val max = 500L
        getConsumers {
            Thread.sleep(300L)
        }.forEach { consumer ->
            val time = runBlocking {
                measureTimeMillis {
                    consumer.await()
                }
            }
            assertTrue(max > time,
                "Expected max to be greater than time: ${time}ms (type: ${consumer::class.simpleName})")
        }
    }

    @Test
    fun `Test timeout of await`() {
        val max = 500L
        getConsumers {
            Thread.sleep(600L)
        }.forEach { consumer ->
            val time = runBlocking {
                measureTimeMillis {
                    consumer.awaitWithSafeTimeout(300L)
                }
            }
            assertTrue(max > time,
                "Expected max to be greater than time: ${time}ms (type: ${consumer::class.simpleName})")
        }
    }
}