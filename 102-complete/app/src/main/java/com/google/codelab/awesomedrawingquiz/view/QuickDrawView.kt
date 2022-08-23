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

package com.google.codelab.awesomedrawingquiz.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.google.codelab.awesomedrawingquiz.data.Drawing
import com.google.codelab.awesomedrawingquiz.data.Stroke
import com.google.gson.Gson

class QuickDrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  private val gson = Gson()

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    strokeWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 1f,
        context.resources.displayMetrics
    )
    style = Paint.Style.STROKE
  }

  private val path = Path()

  private val mtrx = Matrix()

  private val originalRect = RectF(0f, 0f, 200f, 200f)

  private val targetRect = RectF(0f, 0f, 200f, 200f)

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val padding = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, PADDING_DP,
        context.resources.displayMetrics
    ).toInt()

    targetRect.set(
        padding.toFloat(), padding.toFloat(),
        (w - padding * 2).toFloat(), (h - padding * 2).toFloat()
    )
    mtrx.setRectToRect(originalRect, targetRect, Matrix.ScaleToFit.CENTER)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    setMeasuredDimension(
        resolveSize(200, widthMeasureSpec),
        resolveSize(200, heightMeasureSpec)
    )
  }

  fun draw(drawing: Drawing) {
    Log.d(TAG, "Drawing set to " + drawing.word)
    if (!path.isEmpty) {
      path.reset()
    }

    val strokes = Stroke.parseStrokes(gson, drawing)

    for (stroke in strokes) {
      path.moveTo(stroke.x[0].toFloat(), stroke.y[0].toFloat())

      val coordsLength = stroke.x.size
      for (i in 1 until coordsLength) {
        path.lineTo(stroke.x[i].toFloat(), stroke.y[i].toFloat())
      }
    }

    invalidate()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    with(canvas) {
      save()
      matrix = mtrx
      drawPath(path, paint)
      restore()
    }
  }

  companion object {

    private const val TAG = "QuickDrawView"

    private const val PADDING_DP = 48f
  }
}
