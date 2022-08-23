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

import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.io.FileReader

class StrokeTest {

    @Test
    fun parseStrokes() {
        val gson = GsonBuilder()
                .registerTypeAdapter(Drawing::class.java, Drawing.GsonTypeAdapter())
                .create()

        val d = gson.fromJson<Drawing>(FileReader(
                getTestResourceFilePath("item.ndjson")), Drawing::class.java)

        val strokes = Stroke.parseStrokes(gson, d)

        // Total strokes
        assertEquals(2, strokes.size)

        // First stroke
        val stroke1 = strokes[0]
        // X coordinates of first stroke
        val stroke1_xcoords = stroke1.x
        assertEquals(5, stroke1_xcoords.size)
        // verify X coordinate values
        assertEquals(122, stroke1_xcoords[0])
        assertEquals(102, stroke1_xcoords[1].toLong())
        assertEquals(56, stroke1_xcoords[2].toLong())
        assertEquals(45, stroke1_xcoords[3].toLong())
        assertEquals(66, stroke1_xcoords[4].toLong())
        // Y coordinates of first stroke
        val stroke1_ycoords = stroke1.y
        assertEquals(5, stroke1_ycoords.size.toLong())
        // verify Y coordinates of first stroke
        assertEquals(0, stroke1_ycoords[0].toLong())
        assertEquals(3, stroke1_ycoords[1].toLong())
        assertEquals(3, stroke1_ycoords[2].toLong())
        assertEquals(29, stroke1_ycoords[3].toLong())
        assertEquals(22, stroke1_ycoords[4].toLong())

        // Second stroke
        val stroke2 = strokes[1]
        // X coordinates of first stroke
        val stroke2_xcoords = stroke2.x
        assertEquals(5, stroke2_xcoords.size.toLong())
        // verify X coordinate values
        assertEquals(112, stroke2_xcoords[0].toLong())
        assertEquals(121, stroke2_xcoords[1].toLong())
        assertEquals(167, stroke2_xcoords[2].toLong())
        assertEquals(222, stroke2_xcoords[3].toLong())
        assertEquals(224, stroke2_xcoords[4].toLong())
        // Y coordinates of first stroke
        val stroke2_ycoords = stroke2.y
        assertEquals(5, stroke2_ycoords.size.toLong())
        // verify Y coordinates of first stroke
        assertEquals(4, stroke2_ycoords[0].toLong())
        assertEquals(2, stroke2_ycoords[1].toLong())
        assertEquals(2, stroke2_ycoords[2].toLong())
        assertEquals(7, stroke2_ycoords[3].toLong())
        assertEquals(44, stroke2_ycoords[4].toLong())
    }

    private fun getTestResourceFilePath(fileNameInPath: String): String {
        val file = checkNotNull(javaClass.classLoader?.getResource(fileNameInPath)?.file)
        return File(file).absolutePath
    }
}