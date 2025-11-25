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

package me.dkim19375.dkimcore.file

import java.io.File
import java.lang.IllegalStateException
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.writeText
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.dkim19375.dkimcore.exception.ConfigurationException
import me.dkim19375.dkimcore.extension.atomicReference
import me.dkim19375.dkimcore.extension.createInstance

abstract class ObjectDataFile<T : Any>(
    file: File,
    protected val type: KClass<T>,
    protected val default: () -> T = type::createInstance,
    protected val delegateAutoSave: Boolean = true,
    protected val ioCoroutineContext: CoroutineContext? = null,
    protected val forceIOCoroutineContext: Boolean = false,
) : DataFile(file), ReadWriteProperty<Any?, T> {
    private val coroutineScope = ioCoroutineContext?.let { CoroutineScope(it + SupervisorJob()) }

    private var current by atomicReference(default())
    private var readError by atomicReference<Throwable?>()

    open fun get(): T = current

    open fun set(obj: T) {
        current = obj
    }

    /**
     * Note: this function will never throw an exception
     *
     * If exception handling is needed, use [reloadAsync]
     */
    @Synchronized
    override fun reload() = runIO {
        reloadAsync()
        saveAsync()
    }

    /** Note: this may throw an [java.io.IOException] or a [ConfigurationException] */
    open suspend fun reloadAsync() {
        if (forceIOCoroutineContext) {
            val context =
                ioCoroutineContext
                    ?: throw IllegalStateException(
                        "No IO CoroutineContext provided despite forceIOCoroutineContext being true!"
                    )
            withContext(context) { reloadAsync() }
            return
        }
        super.reload()
        runCatching {
                current =
                    file.readText().let { text ->
                        if (text.isBlank()) default() else deserialize(text)
                    }
            }
            .getOrElse {
                readError = it
                throw ConfigurationException(
                    "An error has occurred while trying to read and deserialize the file '${
                        file.path
                    }'",
                    it,
                )
            }
        readError = null
    }

    /**
     * Note: this function will never throw an exception
     *
     * If exception handling is needed, use [saveAsync]
     */
    @Synchronized
    open fun save(obj: T) {
        set(obj)
        save()
    }

    /** Note: this may throw an [java.io.IOException] or a [ConfigurationException] */
    open suspend fun saveAsync(obj: T) {
        set(obj)
        saveAsync()
    }

    /**
     * Note: this function will never throw an exception
     *
     * If exception handling is needed, use [saveAsync]
     */
    @Synchronized override fun save() = runIO { saveAsync() }

    /** Note: this may throw an [java.io.IOException] or a [ConfigurationException] */
    open suspend fun saveAsync() {
        if (forceIOCoroutineContext) {
            val context =
                ioCoroutineContext
                    ?: throw IllegalStateException(
                        "No IO CoroutineContext provided despite forceIOCoroutineContext being true!"
                    )
            withContext(context) { saveAsync() }
            return
        }
        if (readError != null) {
            throw ConfigurationException(
                "Not saving file due to an error when reading and deserializing the file '${
                    file.path
                }'!",
                readError,
            )
        }
        super.save()
        path.writeText(serialize(current))
    }

    protected fun runIO(block: suspend () -> Unit) {
        coroutineScope?.launch { block() } ?: runBlocking { block() }
    }

    protected abstract fun deserialize(text: String): T

    protected abstract fun serialize(obj: T): String

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        if (delegateAutoSave) {
            save(value)
        } else {
            set(value)
        }
}
