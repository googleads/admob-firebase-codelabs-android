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

package com.google.codelab.awesomedrawingquiz.viewmodel

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.codelab.awesomedrawingquiz.data.DatabaseProvider
import com.google.codelab.awesomedrawingquiz.ui.game.GameSettings
import com.google.codelab.awesomedrawingquiz.ui.game.GameViewModel
import com.google.codelab.awesomedrawingquiz.ui.splash.SplashViewModel

class AwesomeDrawingQuizViewModelFactory(
    context: Context,
    private val gameSettings: GameSettings,
    // TODO: Accept FirebaseAnalytics instance as a parameter (101)
) : ViewModelProvider.Factory {

    private val assetManager = context.assets

    private val drawingDao = DatabaseProvider.provideDrawingDao(context)

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            // TODO: Pass FirebaseAnalytics instance as a parameter (101)
            return GameViewModel(drawingDao, gameSettings) as T
        } else if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(assetManager, preferences, drawingDao) as T
        }
        throw IllegalArgumentException("unknown model class $modelClass")
    }
}
