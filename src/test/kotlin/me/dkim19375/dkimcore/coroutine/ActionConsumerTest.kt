package me.dkim19375.dkimcore.coroutine

import kotlin.system.measureTimeMillis
import kotlin.test.*

internal class ActionConsumerTest {
    @Test
    fun `Test time of queue`() {
        val max = 100L
        val time = measureTimeMillis {
            ActionConsumer { Thread.sleep(300L) }.queue()
        }
        assertTrue(max > time)
    }

    @Test
    fun `Test time of complete`() {
        val max = 100L
        val time = measureTimeMillis {
            ActionConsumer { Thread.sleep(300L) }.complete()
        }
        assertTrue(max < time)
    }

    @Test
    fun `Test time of submit`() {
        val max = 100L
        val time = measureTimeMillis {
            ActionConsumer consumer@{
                Thread.sleep(300L)
                return@consumer true
            }.submit().get()
        }
        assertTrue(max < time)
    }
}