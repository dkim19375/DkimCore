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

import me.dkim19375.dkimcore.annotation.API
import me.dkim19375.dkimcore.extension.createFileAndDirs
import me.dkim19375.dkimcore.extension.toPath
import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.SettingsManager
import me.mattstudios.config.properties.Property
import java.io.File
import java.nio.file.Path

@API
open class YamlFile(@API val properties: SettingsHolder, @API val fileName: String) {
    @API
    val path: Path = fileName.toPath()
    @API
    val file: File
        get() = path.toFile()
    @API
    val manager: SettingsManager = SettingsManager.from(file).configurationData(properties.javaClass).create()

    @API
    fun <T : Any> get(property: Property<T>): T = manager.get(property)

    @API
    fun <T : Any> set(property: Property<T>, value: T) = manager.set(property, value)

    @API
    fun reload() {
        path.createFileAndDirs()
        manager.reload()
    }

    @API
    fun save() {
        path.createFileAndDirs()
        manager.save()
    }
}