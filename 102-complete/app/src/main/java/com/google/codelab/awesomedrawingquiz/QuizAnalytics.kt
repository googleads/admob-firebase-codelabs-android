package com.google.codelab.awesomedrawingquiz

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

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
    logEvent(FirebaseAnalytics.Event.LEVEL_START) {
        param(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
    }
}

fun FirebaseAnalytics.logLevelWrongAnswer(levelName: String) {
    logEvent(EVENT_LEVEL_WRONG_ANSWER) {
        param(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
    }
}

fun FirebaseAnalytics.logAdRewardPrompt(adUnitId: String) {
    logEvent(EVENT_AD_REWARD_PROMPT) {
        param(PARAM_AD_UNIT_ID, adUnitId)
    }
}

fun FirebaseAnalytics.logAdRewardImpression(adUnitId: String) {
    logEvent(EVENT_AD_REWARD_IMPRESSION) {
        param(PARAM_AD_UNIT_ID, adUnitId)
    }
}

fun FirebaseAnalytics.logLevelSuccess(
    levelName: String,
    numberOfAttempts: Int,
    elapsedTimeSec: Int,
    hintUsed: Boolean
) {
    logEvent(EVENT_LEVEL_SUCCESS) {
        param(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
        param(PARAM_NUMBER_OF_ATTEMPTS, numberOfAttempts.toLong())
        param(PARAM_ELAPSED_TIME_SEC, elapsedTimeSec.toLong())
        param(PARAM_HINT_USED, if (hintUsed) 1 else 0)
    }
}

fun FirebaseAnalytics.logLevelFail(
    levelName: String,
    numberOfAttempts: Int,
    elapsedTimeSec: Int,
    hintUsed: Boolean
) {
    logEvent(EVENT_LEVEL_FAIL) {
        param(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
        param(PARAM_NUMBER_OF_ATTEMPTS, numberOfAttempts.toLong())
        param(PARAM_ELAPSED_TIME_SEC, elapsedTimeSec.toLong())
        param(PARAM_HINT_USED, if (hintUsed) 1 else 0)
    }
}

fun FirebaseAnalytics.logGameComplete(
    numberOfCorrectAnswers: Int
) {
    logEvent(EVENT_GAME_COMPLETE) {
        param(PARAM_NUMBER_OF_CORRECT_ANSWERS, numberOfCorrectAnswers.toLong())
    }
}
