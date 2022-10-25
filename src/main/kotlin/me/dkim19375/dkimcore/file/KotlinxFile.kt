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

package me.dkim19375.dkimcore.file

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import me.dkim19375.dkimcore.extension.createFileAndDirs
import java.io.File
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.writeText
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

open class KotlinxFile<T : Any>(
    private val type: KClass<T>,
    private val format: StringFormat,
    serializer: KSerializer<T>,
    file: File,
    private val serializationStrategy: SerializationStrategy<T> = serializer,
    private val deserializationStrategy: DeserializationStrategy<T> = serializer,
    private val default: () -> T = default@{
        type.createInstance()
    },
) : DataFile(file) {

    private val current: AtomicReference<T>

    init {
        file.createFileAndDirs()
        current = AtomicReference(file.readText().let { text ->
            if (text.isBlank()) default() else format.decodeFromString(deserializationStrategy, text)
        })
        save()
    }

    open fun get(): T = current.get()

    open fun set(obj: T) = current.set(obj)

    @Synchronized
    override fun reload() {
        super.reload()
        current.set(file.readText().let { text ->
            if (text.isBlank()) default() else format.decodeFromString(deserializationStrategy, text)
        })
    }

    @Synchronized
    open fun save(obj: T) {
        set(obj)
        save()
    }

    @Synchronized
    override fun save() {
        super.save()
        path.writeText(format.encodeToString(serializationStrategy, current.get()))
    }
}