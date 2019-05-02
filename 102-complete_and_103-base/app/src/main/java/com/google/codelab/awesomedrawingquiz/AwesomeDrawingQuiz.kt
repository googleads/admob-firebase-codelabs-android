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

package com.google.codelab.awesomedrawingquiz

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.codelab.awesomedrawingquiz.ui.game.GameSettings
import com.google.codelab.awesomedrawingquiz.viewmodel.AwesomeDrawingQuizViewModelFactory
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class AwesomeDrawingQuiz : Application() {

  override fun onCreate() {
    super.onCreate()
    MobileAds.initialize(this, getString(R.string.admob_app_id))
  }

  fun provideViewModelFactory() = AwesomeDrawingQuizViewModelFactory(this, provideGameSettings())

  private fun provideGameSettings() = GameSettings(provideRemoteConfig())

  private fun provideRemoteConfig(): FirebaseRemoteConfig {
    val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
      setConfigSettings(
          FirebaseRemoteConfigSettings.Builder()
              .setDeveloperModeEnabled(BuildConfig.DEBUG)
              .build()
      )
      setDefaults(R.xml.remote_config_defaults)
    }

    val fetchTask = if (BuildConfig.DEBUG) {
      remoteConfig.fetch(0L)
    } else {
      remoteConfig.fetch()
    }

    fetchTask.addOnCompleteListener {
      if (it.isSuccessful) {
        Log.d("AwesomeDrawingQuiz", "Remote config value fetched")
        remoteConfig.activateFetched()
      }
    }
    return remoteConfig
  }
}
