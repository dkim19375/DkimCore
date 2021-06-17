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

package me.dkim19375.dkimcore.extension

import me.dkim19375.dkimcore.annotation.API
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists

@API
fun File.createFileAndDirs() = toPath().createFileAndDirs()

@API
fun Path.createFileAndDirs() {
    if (exists()) {
        return
    }
    parent?.createDirectories()
    try {
        createFile()
    } catch (_: FileAlreadyExistsException) {
    }
}

@API
fun String.toFile(): File {
    runCatchingOrNull { File(this) }?.run {
        return this
    }
    val array = replace("\\", "/").split("/").toTypedArray()
    val first = array.getOrElse(0) { "" }
    val rest = array.drop(1)
    return Paths.get(first, *rest.toTypedArray()).toFile()
}

@API
fun String.toPath(): Path = toFile().toPath()