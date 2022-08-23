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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

@Entity(tableName = "drawings")
class Drawing(
    @PrimaryKey @ColumnInfo(name = "key_id") var keyId: String,
    var word: String,
    @ColumnInfo(name = "countrycode") var countryCode: String,
    var timestamp: String,
    var isRecognized: Boolean,
    var drawing: String
) {

  class GsonTypeAdapter : TypeAdapter<Drawing>() {
    private val stringAdapter: TypeAdapter<String>

    private val booleanAdapter: TypeAdapter<Boolean>

    private val jsonArrayAdapter: TypeAdapter<JsonArray>

    init {
      with(Gson()) {
        stringAdapter = getAdapter(String::class.java)
        booleanAdapter = getAdapter(Boolean::class.java)
        jsonArrayAdapter = getAdapter(JsonArray::class.java)
      }
    }

    override fun write(jsonWriter: JsonWriter, obj: Drawing?) {
      if (obj == null) {
        jsonWriter.nullValue()
        return
      }

      jsonWriter.beginObject()
      jsonWriter.name("key_id")
      stringAdapter.write(jsonWriter, obj.keyId)
      jsonWriter.name("word")
      stringAdapter.write(jsonWriter, obj.word)
      jsonWriter.name("countrycode")
      stringAdapter.write(jsonWriter, obj.countryCode)
      jsonWriter.name("timestamp")
      stringAdapter.write(jsonWriter, obj.timestamp)
      jsonWriter.name("recognized")
      booleanAdapter.write(jsonWriter, obj.isRecognized)
      jsonWriter.name("drawing")
      jsonArrayAdapter.write(jsonWriter, jsonArrayAdapter.fromJson(obj.drawing))

      jsonWriter.endObject()
    }

    override fun read(jsonReader: JsonReader): Drawing? {
      if (jsonReader.peek() == JsonToken.NULL) {
        jsonReader.nextNull()
        return null
      }

      jsonReader.beginObject()

      var keyId: String? = null
      var word: String? = null
      var countryCode: String? = null
      var timestamp: String? = null
      var recognized = false
      var drawing: String? = null

      while (jsonReader.hasNext()) {
        val key = jsonReader.nextName()
        if (jsonReader.peek() == JsonToken.NULL) {
          jsonReader.nextNull()
          continue
        }
        when (key) {
          "key_id" -> {
            keyId = stringAdapter.read(jsonReader)
          }
          "word" -> {
            word = stringAdapter.read(jsonReader)
          }
          "countrycode" -> {
            countryCode = stringAdapter.read(jsonReader)
          }
          "timestamp" -> {
            timestamp = stringAdapter.read(jsonReader)
          }
          "recognized" -> {
            recognized = booleanAdapter.read(jsonReader)
          }
          "drawing" -> {
            drawing = jsonArrayAdapter.read(jsonReader).toString()
          }
          else -> {
            jsonReader.skipValue()
          }
        }
      }
      jsonReader.endObject()

      return Drawing(keyId!!, word!!, countryCode!!, timestamp!!, recognized, drawing!!)
    }
  }
}
