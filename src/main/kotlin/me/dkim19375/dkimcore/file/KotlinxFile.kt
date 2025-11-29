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
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import me.dkim19375.dkimcore.extension.createInstance

open class KotlinxFile<T : Any>(
    type: KClass<T>,
    private val format: StringFormat,
    serializer: KSerializer<T>,
    file: File,
    private val serializationStrategy: SerializationStrategy<T> = serializer,
    private val deserializationStrategy: DeserializationStrategy<T> = serializer,
    delegateAutoSave: Boolean = true,
    ioCoroutineContext: CoroutineContext? = Dispatchers.IO,
    forceIOCoroutineContext: Boolean = false,
    default: () -> T = type::createInstance,
) :
    ObjectDataFile<T>(
        file,
        type,
        default,
        delegateAutoSave,
        ioCoroutineContext,
        forceIOCoroutineContext,
    ) {
    init {
        super.reload()
        super.save()
    }

    override fun deserialize(text: String): T =
        format.decodeFromString(deserializationStrategy, text)

    override fun serialize(obj: T): String = format.encodeToString(serializationStrategy, obj)
}
