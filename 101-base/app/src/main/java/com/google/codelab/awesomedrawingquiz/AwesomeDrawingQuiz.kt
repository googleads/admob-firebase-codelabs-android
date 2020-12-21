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
        // TODO: Pass FirebaseAnalytics instance as a parameter (101)
    )

    // TODO: Provide FirebaseAnalytics instance (101)


    // TODO: Provide FirebaseRemoteConfig instance (102)
    private fun provideGameSettings() = GameSettings()

    // TODO: Add a function that provides a FirebaseRemoteConfig instance (102)

}
