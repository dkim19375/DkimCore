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
    val array = replace("\\", "/").split("/").toTypedArray()
    val first = array.getOrElse(0) { "" }
    val rest = array.drop(1)
    return Paths.get(first, *rest.toTypedArray()).toFile()
}

@API
fun String.toPath(): Path = toFile().toPath()