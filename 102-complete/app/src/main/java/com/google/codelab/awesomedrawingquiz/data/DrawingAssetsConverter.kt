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
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicBoolean

class DrawingAssetsConverter(private val am: AssetManager) : Observable<Drawing>() {

  private val gson = GsonBuilder()
      .registerTypeAdapter(Drawing::class.java, Drawing.GsonTypeAdapter())
      .create()

  override fun subscribeActual(observer: Observer<in Drawing>) {
    val listener = Listener(observer)
    observer.onSubscribe(listener)

    try {
      val files = am.list("drawings")

      if (null != files) {
        for (path in files) {
          val inputStream = am.open("drawings/$path")
          val reader = BufferedReader(
              InputStreamReader(am.open("drawings/$path"))
          )

          var line: String?
          do {
            line = reader.readLine()
            val drawing = gson.fromJson(line, Drawing::class.java)

            if (null != drawing) {
              listener.onReadDrawing(drawing)
            }
          } while (null != line)

          inputStream.close()
          reader.close()
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }

    listener.onFinish()
  }

  private interface TaskListener {

    fun onReadDrawing(d: Drawing)

    fun onFinish()
  }

  internal class Listener(
      private val observer: Observer<in Drawing>
  ) : TaskListener, Disposable {

    private val unsubscribed = AtomicBoolean()

    override fun onReadDrawing(d: Drawing) {
      if (!isDisposed) {
        observer.onNext(d)
      }
    }

    override fun onFinish() {
      if (!isDisposed) {
        observer.onComplete()
      }
    }

    override fun dispose() {
      unsubscribed.compareAndSet(false, true)
    }

    override fun isDisposed(): Boolean {
      return unsubscribed.get()
    }
  }
}
