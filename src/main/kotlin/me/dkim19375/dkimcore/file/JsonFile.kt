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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.dkim19375.dkimcore.annotation.API
import me.dkim19375.dkimcore.extension.createFileAndDirs
import java.lang.reflect.Type
import kotlin.io.path.reader
import kotlin.io.path.writer
import kotlin.reflect.KClass

open class JsonFile<T : Any>(
    private val type: KClass<T>,
    fileName: String,
    prettyPrinting: Boolean = true,
    typeAdapters: Map<Type, Any> = emptyMap(),
    private val default: () -> T = { type.java.getDeclaredConstructor().newInstance() },
    @API val gson: Gson = GsonBuilder()
        .apply {
            if (prettyPrinting) {
                setPrettyPrinting()
            }
            typeAdapters.forEach(this::registerTypeAdapter)
        }.create()
) : DataFile(fileName) {

    private var current: T

    init {
        path.createFileAndDirs()
        current = gson.fromJson(path.reader(), type.java) ?: run {
            val new = default()
            set(new)
            save()
            new
        }
    }

    open fun get(): T = current

    open fun set(obj: T) {
        current = obj
    }

    override fun reload() {
        super.reload()
        current = gson.fromJson(path.reader(), type.java) ?: run {
            val new = type.java.getDeclaredConstructor().newInstance()
            set(new)
            save()
            new
        }
    }

    override fun save() {
        super.save()
        path.writer().use {
            gson.toJson(current, type.java, it)
        }
    }
}