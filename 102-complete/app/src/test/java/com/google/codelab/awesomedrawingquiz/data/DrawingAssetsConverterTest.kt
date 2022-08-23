/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codelab.awesomedrawingquiz.data

import android.content.res.AssetManager
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.io.File
import java.io.InputStream

class DrawingAssetsConverterTest {

    @Mock
    private lateinit var am: AssetManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun loadItem() {
        val pathNames = arrayOf(getTestResourceFilePath("item.ndjson"))

        // Mock
        am = mock(AssetManager::class.java).apply {
            `when`(list(ArgumentMatchers.anyString()))
                .thenReturn(pathNames)
            `when`(open(ArgumentMatchers.anyString()))
                .thenReturn(getTestResource("item.ndjson"))
        }

        with(TestObserver.create<Drawing>()) {
            // When
            DrawingAssetsConverter(am).subscribe(this)

            // Then
            assertComplete()
            assertNoErrors()
            assertValueCount(1)

            dispose()
        }

    }

    private fun getTestResourceFilePath(fileNameInPath: String): String {
        val file = checkNotNull(javaClass.classLoader?.getResource(fileNameInPath)?.file)
        return File(file).absolutePath
    }

    private fun getTestResource(path: String)
            = checkNotNull(javaClass.classLoader?.getResourceAsStream(path))

}