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
import java.io.File
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.reader
import kotlin.io.path.writer
import kotlin.reflect.KClass

open class JsonFile<T : Any>(
    private val type: KClass<T>,
    file: File,
    prettyPrinting: Boolean = true,
    typeAdapters: Map<Class<*>, Any> = emptyMap(),
    complexMapSerialization: Boolean = true,
    private val default: () -> T = { type.java.getDeclaredConstructor().newInstance() },
    val extraGson: GsonBuilder.() -> GsonBuilder = { this },
    @API val gson: Gson = GsonBuilder()
        .apply {
            if (prettyPrinting) {
                setPrettyPrinting()
            }
            if (complexMapSerialization) {
                enableComplexMapKeySerialization()
            }
            typeAdapters.forEach(this::registerTypeHierarchyAdapter)
            extraGson()
        }.create(),
) : DataFile(file) {

    private val current: AtomicReference<T>

    init {
        path.createFileAndDirs()
        var save = false
        current = AtomicReference(try {
            path.reader().use { gson.fromJson(it, type.java) }
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        } ?: run {
            save = true
            default()
        })
        if (save) {
            save()
        }
    }

    open fun get(): T = current.get()

    open fun set(obj: T) = current.set(obj)

    @Synchronized
    override fun reload() {
        super.reload()
        current.set(try {
            path.reader().use { gson.fromJson(it, type.java) }
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        } ?: run {
            val new = type.java.getDeclaredConstructor().newInstance()
            set(new)
            save()
            new
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
        path.writer().use {
            gson.toJson(current.get(), type.java, it)
        }
    }
}