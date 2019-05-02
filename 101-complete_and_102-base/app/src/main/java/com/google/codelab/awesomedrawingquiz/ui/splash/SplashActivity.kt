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

package com.google.codelab.awesomedrawingquiz.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.codelab.awesomedrawingquiz.AwesomeDrawingQuiz
import com.google.codelab.awesomedrawingquiz.R
import com.google.codelab.awesomedrawingquiz.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

  private val viewModel by lazy {
    ViewModelProviders.of(
        this, (application as AwesomeDrawingQuiz).provideViewModelFactory()
    )[SplashViewModel::class.java]
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    viewModel.importDrawingsIfRequired {
      ActivityCompat.finishAffinity(this)
      startActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }
  }
}
