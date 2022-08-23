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


import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.codelab.awesomedrawingquiz.data.DrawingAssetsConverter
import com.google.codelab.awesomedrawingquiz.data.DrawingDao
import com.google.codelab.awesomedrawingquiz.data.DrawingsDbImporter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashViewModel(
    private val assetManager: AssetManager,
    private val preferences: SharedPreferences,
    private val dao: DrawingDao
) : ViewModel() {

  private var disposable: Disposable? = null

  internal fun importDrawingsIfRequired(callback: () -> Unit) {
    // if there is an ongoing task, don't start another one.
    if (null != disposable) {
      return
    }

    val signal: Completable

    if (!preferences.getBoolean(KEY_INITIALIZED, false)) {
      Log.d(TAG, "Database is not ready")
      signal = DrawingAssetsConverter(assetManager).toList()
          .flatMapCompletable { DrawingsDbImporter(dao, it) }
          .doOnComplete { preferences.edit().putBoolean(KEY_INITIALIZED, true).apply() }
    } else {
      Log.d(TAG, "Database is ready to use")
      // add 1 second delay even if we already imported all of the drawings,
      // just to earn some time to show nice splash screen!
      signal = Completable.timer(1, TimeUnit.SECONDS, Schedulers.io())
    }

    disposable = signal.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback)
  }

  override fun onCleared() {
    if (null != disposable) {
      disposable!!.dispose()
    }
  }

  companion object {

    private val TAG = "SplashViewModel"

    private val KEY_INITIALIZED = "db_initialized"
  }
}
