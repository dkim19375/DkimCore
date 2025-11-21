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
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.writeText
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.dkim19375.dkimcore.exception.ConfigurationException
import me.dkim19375.dkimcore.extension.atomicReference
import me.dkim19375.dkimcore.extension.createInstance

abstract class ObjectDataFile<T : Any>(
    file: File,
    protected val type: KClass<T>,
    protected val default: () -> T = type::createInstance,
    protected val delegateAutoSave: Boolean = true,
    protected val ioCoroutineContext: CoroutineContext? = null,
) : DataFile(file), ReadWriteProperty<Any?, T> {
    private var coroutineScope by
        atomicReference<CoroutineScope?>(ioCoroutineContext?.let(::CoroutineScope))

    private var current by atomicReference(default())
    private var readError by atomicReference<Throwable?>()

    open fun get(): T = current

    open fun set(obj: T) {
        current = obj
    }

    @Synchronized
    override fun reload() {
        runIO {
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
        save()
    }

    @Synchronized
    open fun save(obj: T) {
        set(obj)
        save()
    }

    @Synchronized
    override fun save() {
        if (readError != null) {
            throw ConfigurationException(
                "Not saving file due to an error when reading and deserializing the file '${
                    file.path
                }'!",
                readError,
            )
        }
        val serialized = serialize(current)
        runIO {
            super.save()
            path.writeText(serialized)
        }
    }

    private fun runIO(block: () -> Unit) {
        var scope = coroutineScope
        if (scope == null) {
            block()
            return
        }
        if (!scope.isActive) {
            scope = ioCoroutineContext?.let(::CoroutineScope)
            coroutineScope = scope
        }
        scope?.launch { block() } ?: block()
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
