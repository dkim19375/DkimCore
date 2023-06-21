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

import me.dkim19375.dkimcore.exception.ConfigurationException
import me.dkim19375.dkimcore.extension.createFileAndDirs
import java.io.File
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private val TEST_FILE: File = Paths.get("build", "tests", "test.json").toFile()
private const val VALUE_1 = 1
private const val VALUE_1_ALT = 5
private const val VALUE_2 = "testing"
private const val VALUE_2_ALT = "testing #2"
private val VALUE_MAP_1 = mapOf(1 to 2, 3 to 4)
private val VALUE_MAP_1_ALT = mapOf(4 to 5, 6 to 7)
private val VALUE_MAP_2 = mapOf("key #1-1" to "value #1-1", "key #1-2" to "value #1-2")
private val VALUE_MAP_2_ALT = mapOf("key #2-1" to "value #2-1", "key #2-2" to "value #2-2")
private val INVALID_DATA = """
    This is not valid data: [}
""".trimIndent()

class JsonFileTest {
    private class TestClass {
        var value1 = VALUE_1
        var value2 = VALUE_2
    }

    private class MapTestClass {
        var map1 = VALUE_MAP_1
        var map2 = VALUE_MAP_2
    }

    @Test
    fun `Automatic file creation`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        JsonFile(TestClass::class, TEST_FILE)
        assertTrue(TEST_FILE.exists())
    }

    @Test
    fun `File creation on save`() {
        val file = JsonFile(TestClass::class, TEST_FILE)
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        file.save()
        assertTrue(TEST_FILE.exists())
    }

    @Test
    fun `File creation on reload`() {
        val file = JsonFile(TestClass::class, TEST_FILE)
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        file.reload()
        assertTrue(TEST_FILE.exists())
    }

    @Test
    fun `Automatic instance creation`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        val file = JsonFile(TestClass::class, TEST_FILE)
        assertEquals(VALUE_1, file.get().value1)
        assertEquals(VALUE_2, file.get().value2)
    }

    @Test
    fun `Testing reload and set`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        val file = JsonFile(TestClass::class, TEST_FILE)
        val file2 = JsonFile(TestClass::class, TEST_FILE)
        val test = TestClass()
        test.value1 = VALUE_1_ALT
        test.value2 = VALUE_2_ALT
        file.set(test)
        assertEquals(VALUE_1_ALT, file.get().value1)
        assertEquals(VALUE_2_ALT, file.get().value2)
        assertNotEquals(VALUE_1_ALT, file2.get().value1)
        assertNotEquals(VALUE_2_ALT, file2.get().value2)
        file2.reload()
        assertNotEquals(VALUE_1_ALT, file2.get().value1)
        assertNotEquals(VALUE_2_ALT, file2.get().value2)
        file.save()
        assertEquals(VALUE_1_ALT, file.get().value1)
        assertEquals(VALUE_2_ALT, file.get().value2)
        assertNotEquals(VALUE_1_ALT, file2.get().value1)
        assertNotEquals(VALUE_2_ALT, file2.get().value2)
        file2.reload()
        assertEquals(VALUE_1_ALT, file2.get().value1)
        assertEquals(VALUE_2_ALT, file2.get().value2)
    }

    @Test
    fun `Testing set & save`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        val file = JsonFile(TestClass::class, TEST_FILE)
        val file2 = JsonFile(TestClass::class, TEST_FILE)
        val test = TestClass()
        test.value1 = VALUE_1_ALT
        test.value2 = VALUE_2_ALT
        file.save(test)
        assertEquals(VALUE_1_ALT, file.get().value1)
        assertEquals(VALUE_2_ALT, file.get().value2)
        assertNotEquals(VALUE_1_ALT, file2.get().value1)
        assertNotEquals(VALUE_2_ALT, file2.get().value2)
        file2.reload()
        assertEquals(VALUE_1_ALT, file2.get().value1)
        assertEquals(VALUE_2_ALT, file2.get().value2)
    }



    @Test
    fun `Automatic instance creation for maps`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        val file = JsonFile(MapTestClass::class, TEST_FILE)
        assertEquals(VALUE_MAP_1, file.get().map1)
        assertEquals(VALUE_MAP_2, file.get().map2)
    }

    @Test
    fun `Testing reload and set for maps`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        val file = JsonFile(MapTestClass::class, TEST_FILE)
        val file2 = JsonFile(MapTestClass::class, TEST_FILE)
        val test = MapTestClass()
        test.map1 = VALUE_MAP_1_ALT
        test.map2 = VALUE_MAP_2_ALT
        file.set(test)
        assertEquals(VALUE_MAP_1_ALT, file.get().map1)
        assertEquals(file.get().map2, VALUE_MAP_2_ALT)
        assertNotEquals(VALUE_MAP_1_ALT, file2.get().map1)
        assertNotEquals(VALUE_MAP_2_ALT, file2.get().map2)
        file2.reload()
        assertNotEquals(VALUE_MAP_1_ALT, file2.get().map1)
        assertNotEquals(VALUE_MAP_2_ALT, file2.get().map2)
        file.save()
        assertEquals(VALUE_MAP_1_ALT, file.get().map1)
        assertEquals(VALUE_MAP_2_ALT, file.get().map2)
        assertNotEquals(VALUE_MAP_1_ALT, file2.get().map1)
        assertNotEquals(VALUE_MAP_2_ALT, file2.get().map2)
        file2.reload()
        assertEquals(VALUE_MAP_1_ALT, file2.get().map1)
        assertEquals(VALUE_MAP_2_ALT, file2.get().map2)
    }

    @Test
    fun `Testing set & save for maps`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        val file = JsonFile(MapTestClass::class, TEST_FILE)
        val file2 = JsonFile(MapTestClass::class, TEST_FILE)
        val test = MapTestClass()
        test.map1 = VALUE_MAP_1_ALT
        test.map2 = VALUE_MAP_2_ALT
        file.save(test)
        assertEquals(VALUE_MAP_1_ALT, file.get().map1)
        assertEquals(VALUE_MAP_2_ALT, file.get().map2)
        assertNotEquals(VALUE_MAP_1_ALT, file2.get().map1)
        assertNotEquals(VALUE_MAP_2_ALT, file2.get().map2)
        file2.reload()
        assertEquals(VALUE_MAP_1_ALT, file2.get().map1)
        assertEquals(VALUE_MAP_2_ALT, file2.get().map2)
    }

    @Test
    fun `Testing error handling`() {
        TEST_FILE.delete()
        assertFalse(TEST_FILE.exists())
        TEST_FILE.createFileAndDirs()
        TEST_FILE.writeText(INVALID_DATA)
        assertEquals(INVALID_DATA, TEST_FILE.readText())
        assertFailsWith<ConfigurationException> {
            JsonFile(MapTestClass::class, TEST_FILE)
        }
        TEST_FILE.writeText("")
        val file = JsonFile(MapTestClass::class, TEST_FILE)
        TEST_FILE.writeText(INVALID_DATA)
        assertEquals(INVALID_DATA, TEST_FILE.readText())
        assertFailsWith<ConfigurationException> {
            file.reload()
        }
        assertFailsWith<ConfigurationException> {
            file.save()
        }
        assertEquals(INVALID_DATA, TEST_FILE.readText())
    }
}