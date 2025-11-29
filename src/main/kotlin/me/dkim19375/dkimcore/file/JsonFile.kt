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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import me.dkim19375.dkimcore.annotation.API
import me.dkim19375.dkimcore.extension.createInstance

open class JsonFile<T : Any>(
    type: KClass<T>,
    file: File,
    prettyPrinting: Boolean = true,
    typeAdapters: Map<Class<*>, Any> = emptyMap(),
    complexMapSerialization: Boolean = true,
    default: () -> T = type::createInstance,
    val extraGson: GsonBuilder.() -> GsonBuilder = { this },
    @API
    val gson: Gson =
        GsonBuilder()
            .apply {
                if (prettyPrinting) {
                    setPrettyPrinting()
                }
                if (complexMapSerialization) {
                    enableComplexMapKeySerialization()
                }
                typeAdapters.forEach(this::registerTypeHierarchyAdapter)
                extraGson()
            }
            .create(),
    delegateAutoSave: Boolean = true,
    ioCoroutineContext: CoroutineContext? = Dispatchers.IO,
    forceIOCoroutineContext: Boolean = false,
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

    override fun deserialize(text: String): T = gson.fromJson(text, type.java)

    override fun serialize(obj: T): String = gson.toJson(obj, type.java)
}
