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
import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.SettingsManager
import me.mattstudios.config.properties.Property
import java.io.File

@API
open class YamlFile(@API val properties: SettingsHolder, file: File) : DataFile(file) {
    @API
    val manager: SettingsManager = SettingsManager.from(this.file).configurationData(properties.javaClass).create()

    @API
    fun <T : Any> get(property: Property<T>): T = manager.get(property)

    @API
    fun <T : Any> set(property: Property<T>, value: T) = manager.set(property, value)

    @API
    override fun reload() {
        super.reload()
        manager.reload()
    }

    @API
    override fun save() {
        super.save()
        manager.save()
    }
}