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
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig

class AwesomeDrawingQuiz : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            Log.d("AwesomeDrawingQuiz", "Mobile Ads SDK initialized")
        }
    }

    fun provideViewModelFactory() = AwesomeDrawingQuizViewModelFactory(
        this,
        provideGameSettings(),
        // COMPLETE: Pass FirebaseAnalytics instance as a parameter (101)
        provideFirebaseAnalytics(),
    )

    // COMPLETE: Provide FirebaseAnalytics instance (101)
    private fun provideFirebaseAnalytics() = Firebase.analytics

    // COMPLETE: Provide FirebaseRemoteConfig instance (102)
    private fun provideGameSettings() = GameSettings(provideRemoteConfig())

    // COMPLETE: Add a function that provides a FirebaseRemoteConfig instance (102)
    private fun provideRemoteConfig(): FirebaseRemoteConfig {
        val rc = Firebase.remoteConfig.apply {
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
        val fetchTask = if (BuildConfig.DEBUG) rc.fetch(0L) else rc.fetch()
        fetchTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("AwesomeDrawingQuiz", "Remote config value fetched")
                rc.activate()
            }
        }
        return rc
    }
}
