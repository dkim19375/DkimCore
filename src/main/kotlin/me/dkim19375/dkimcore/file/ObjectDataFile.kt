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

import me.dkim19375.dkimcore.exception.ConfigurationException
import me.dkim19375.dkimcore.extension.atomicReference
import me.dkim19375.dkimcore.extension.createInstance
import java.io.File
import kotlin.io.path.writeText
import kotlin.reflect.KClass

abstract class ObjectDataFile<T : Any>(
    file: File,
    protected val type: KClass<T>,
    protected val default: () -> T = type::createInstance,
) : DataFile(file) {

    private var current by atomicReference(default())
    private var readError by atomicReference<Throwable?>()

    open fun get(): T = current

    open fun set(obj: T) {
        current = obj
    }

    @Synchronized
    override fun reload() {
        super.reload()
        runCatching {
            current = file.readText().let { text ->
                if (text.isBlank()) default() else deserialize(text)
            }
        }.getOrElse {
            readError = it
            throw ConfigurationException(
                "An error has occurred while trying to read and deserialize the file '${
                    file.path
                }'", it
            )
        }
        readError = null
        save()
    }


    @Synchronized
    open fun save(obj: T) {
        set(obj)
        save()
    }

    @Synchronized
    override fun save() {
        super.save()
        if (readError != null) {
            throw ConfigurationException(
                "Not saving file due to an error when reading and deserializing the file '${
                    file.path
                }'!", readError
            )
        }
        path.writeText(serialize(current))
    }

    protected abstract fun deserialize(text: String): T

    protected abstract fun serialize(obj: T): String
}