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

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

private const val EVENT_AD_REWARD_PROMPT = "ad_reward_prompt"

private const val EVENT_AD_REWARD_IMPRESSION = "ad_reward_impression"

private const val EVENT_LEVEL_FAIL = "level_fail"

private const val EVENT_LEVEL_SUCCESS = "level_success"

private const val EVENT_LEVEL_WRONG_ANSWER = "level_wrong_answer"

private const val EVENT_GAME_START = "game_start"

private const val EVENT_GAME_COMPLETE = "game_complete"

private const val PARAM_AD_UNIT_ID = "ad_unit_id"

private const val PARAM_ELAPSED_TIME_SEC = "elapsed_time_sec"

private const val PARAM_HINT_USED = "hint_used"

private const val PARAM_NUMBER_OF_ATTEMPTS = "number_of_attempts"

private const val PARAM_NUMBER_OF_CORRECT_ANSWERS = "number_of_correct_answers"

fun FirebaseAnalytics.logGameStart() = logEvent(EVENT_GAME_START, null)

fun FirebaseAnalytics.logLevelStart(levelName: String) {
  logEvent(FirebaseAnalytics.Event.LEVEL_START, Bundle().apply {
    putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
  })
}

fun FirebaseAnalytics.logLevelWrongAnswer(levelName: String) {
  logEvent(EVENT_LEVEL_WRONG_ANSWER, Bundle().apply {
    putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
  })
}

fun FirebaseAnalytics.logAdRewardPrompt(adUnitId: String) {
  logEvent(EVENT_AD_REWARD_PROMPT, Bundle().apply {
    putString(PARAM_AD_UNIT_ID, adUnitId)
  })
}

fun FirebaseAnalytics.logAdRewardImpression(adUnitId: String) {
  logEvent(EVENT_AD_REWARD_IMPRESSION, Bundle().apply {
    putString(PARAM_AD_UNIT_ID, adUnitId)
  })
}

fun FirebaseAnalytics.logLevelSuccess(
    levelName: String,
    numberOfAttempts: Int,
    elapsedTimeSec: Int,
    hintUsed: Boolean
) {
  logEvent(EVENT_LEVEL_SUCCESS, Bundle().apply {
    putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
    putInt(PARAM_NUMBER_OF_ATTEMPTS, numberOfAttempts)
    putInt(PARAM_ELAPSED_TIME_SEC, elapsedTimeSec)
    putInt(PARAM_HINT_USED, if (hintUsed) 1 else 0)
  })
}

fun FirebaseAnalytics.logLevelFail(
    levelName: String,
    numberOfAttempts: Int,
    elapsedTimeSec: Int,
    hintUsed: Boolean
) {
  logEvent(EVENT_LEVEL_FAIL, Bundle().apply {
    putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
    putInt(PARAM_NUMBER_OF_ATTEMPTS, numberOfAttempts)
    putInt(PARAM_ELAPSED_TIME_SEC, elapsedTimeSec)
    putInt(PARAM_HINT_USED, if (hintUsed) 1 else 0)
  })
}

fun FirebaseAnalytics.logGameComplete(
    numberOfCorrectAnswers: Int
) {
  logEvent(EVENT_GAME_COMPLETE, Bundle().apply {
    putInt(PARAM_NUMBER_OF_CORRECT_ANSWERS, numberOfCorrectAnswers)
  })
}
