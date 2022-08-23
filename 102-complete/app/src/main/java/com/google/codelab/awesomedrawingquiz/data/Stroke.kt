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

import com.google.gson.Gson
import com.google.gson.JsonArray

import java.util.LinkedList

class Stroke private constructor(val x: IntArray, val y: IntArray) {
  companion object {

    fun parseStrokes(gson: Gson, drawing: Drawing): List<Stroke> {
      val result = LinkedList<Stroke>()

      val strokesInJson = Gson().fromJson(drawing.drawing, JsonArray::class.java)
      for (strokeElem in strokesInJson) {
        val strokes = strokeElem.asJsonArray
        val x = gson.fromJson(strokes.get(0), IntArray::class.java)
        val y = gson.fromJson(strokes.get(1), IntArray::class.java)

        result.add(Stroke(x, y))
      }

      return result
    }
  }
}
