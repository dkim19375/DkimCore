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
import java.nio.file.Paths
import kotlin.test.*
import me.dkim19375.dkimcore.extension.createFileAndDirs
import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property
import org.junit.jupiter.api.assertDoesNotThrow

private val TEST_FILE: File = Paths.get("build", "tests", "test.yml").toFile()
private const val DEFAULT_VALUE: String = "test"
private const val OTHER_VALUE: String = "other"

internal class YamlFileTest {

    object SettingsHolderTesting : SettingsHolder {
        @Path("test-path") val PROPERTY = Property.create(DEFAULT_VALUE)
    }

    @Test
    fun `Basic file creation`() {
        assertDoesNotThrow(TEST_FILE::createFileAndDirs)
        TEST_FILE.delete()
    }

    @Test
    fun `File creation`() {
        val file = YamlFile(SettingsHolderTesting, TEST_FILE)
        assertTrue(file.file.exists())
        file.file.delete()
    }

    @Test
    fun `Get property`() {
        val file = YamlFile(SettingsHolderTesting, TEST_FILE)
        assertEquals(DEFAULT_VALUE, file.get(SettingsHolderTesting.PROPERTY))
        file.file.delete()
    }

    @Test
    fun `Set and get property`() {
        val file = YamlFile(SettingsHolderTesting, TEST_FILE)
        file.set(SettingsHolderTesting.PROPERTY, OTHER_VALUE)
        file.save()
        assertEquals(OTHER_VALUE, file.get(SettingsHolderTesting.PROPERTY))
        file.file.delete()
    }

    @Test
    fun `Set and get property with new instance`() {
        val file = YamlFile(SettingsHolderTesting, TEST_FILE)
        file.set(SettingsHolderTesting.PROPERTY, OTHER_VALUE)
        file.save()
        val newFile = YamlFile(SettingsHolderTesting, TEST_FILE)
        assertEquals(OTHER_VALUE, newFile.get(SettingsHolderTesting.PROPERTY))
        file.file.delete()
    }

    @Test
    fun `Not instantly reloading property with new instance`() {
        val file = YamlFile(SettingsHolderTesting, TEST_FILE)
        val newFile = YamlFile(SettingsHolderTesting, TEST_FILE)
        file.set(SettingsHolderTesting.PROPERTY, OTHER_VALUE)
        file.save()
        assertEquals(DEFAULT_VALUE, newFile.get(SettingsHolderTesting.PROPERTY))
        file.file.delete()
    }

    @Test
    fun `Reloading property with new instance`() {
        val file = YamlFile(SettingsHolderTesting, TEST_FILE)
        val newFile = YamlFile(SettingsHolderTesting, TEST_FILE)
        file.set(SettingsHolderTesting.PROPERTY, OTHER_VALUE)
        file.save()
        newFile.reload()
        assertEquals(OTHER_VALUE, newFile.get(SettingsHolderTesting.PROPERTY))
        file.file.delete()
    }

    @Test
    fun `Testing set & save`() {
        val file = YamlFile(SettingsHolderTesting, TEST_FILE)
        val newFile = YamlFile(SettingsHolderTesting, TEST_FILE)
        file.save(SettingsHolderTesting.PROPERTY, OTHER_VALUE)
        newFile.reload()
        assertEquals(OTHER_VALUE, newFile.get(SettingsHolderTesting.PROPERTY))
        file.file.delete()
    }
}
