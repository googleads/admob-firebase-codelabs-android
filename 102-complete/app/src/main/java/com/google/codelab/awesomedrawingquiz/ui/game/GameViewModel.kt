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

package com.google.codelab.awesomedrawingquiz.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.codelab.awesomedrawingquiz.*
import com.google.codelab.awesomedrawingquiz.data.Drawing
import com.google.codelab.awesomedrawingquiz.data.DrawingDao
import com.google.codelab.awesomedrawingquiz.ui.game.GameSettings.Companion.MAX_GAME_LEVEL
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*

class GameViewModel(
    private val drawingDao: DrawingDao,
    private val settings: GameSettings,
    // COMPLETE: Accept FirebaseAnalytics instance as a parameter (101)
    private val analytics: FirebaseAnalytics,
) : ViewModel() {

    private var drawingRequestDisposable: Disposable? = null

    private val gameEventDisposable = CompositeDisposable()

    private val gameEvents = PublishSubject.create<GameEvent>()

    // Level-scoped information

    val isHintAvailable: Boolean
        get() = !isHintUsed

    private var currentLevel = 1

    private var numAttempts = 0

    private var disclosedLettersByDefault = 1

    private var disclosedLetters: Int = 0

    private var levelStartTimeInMillis: Long = 0

    private var isHintUsed: Boolean = false

    private lateinit var clue: String

    private lateinit var drawing: Drawing

    // Game-scoped information

    private var numCorrectAnswers = 0

    private val seenWords = ArrayList<String>()

    fun registerGameEventListener(listener: (GameEvent) -> Unit) {
        gameEventDisposable.add(
            gameEvents
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener)
        )
    }

    fun startGame() {
        numCorrectAnswers = 0
        seenWords.clear()

        // COMPLETE: Log game_start event (101)
        analytics.logGameStart()

        startLevel(1)
    }

    fun checkAnswer(userAnswer: String) {
        numAttempts++

        val correct = drawing.word.equals(userAnswer, true)
        if (correct) {
            numCorrectAnswers++
            val elapsedTimeInSeconds =
                (System.currentTimeMillis() - levelStartTimeInMillis).toInt() / 1000

            // COMPLETE: Log level_success event (101)
            analytics.logLevelSuccess(
                levelName = drawing.word,
                numberOfAttempts = numAttempts,
                elapsedTimeSec = elapsedTimeInSeconds,
                hintUsed = isHintUsed,
            )

            gameEvents.onNext(
                LevelClearEvent(
                    numAttempts, elapsedTimeInSeconds,
                    currentLevel == MAX_GAME_LEVEL, isHintUsed, drawing,
                )
            )
        } else {
            // COMPLETE: Log level_wrong_answer event (101)
            analytics.logLevelWrongAnswer(levelName = drawing.word)

            gameEvents.onNext(WrongAnswerEvent(drawing))
        }
    }

    fun skipLevel() {
        val elapsedTimeInSeconds =
            (System.currentTimeMillis() - levelStartTimeInMillis).toInt() / 1000

        // COMPLETE: Log level_fail event (101)
        analytics.logLevelFail(
            levelName = drawing.word,
            numberOfAttempts = numAttempts,
            elapsedTimeSec = elapsedTimeInSeconds,
            hintUsed = isHintUsed,
        )

        gameEvents.onNext(
            LevelSkipEvent(numAttempts, elapsedTimeInSeconds, isHintUsed, drawing)
        )
        moveToNextLevel()
    }

    fun moveToNextLevel() {
        if (currentLevel < MAX_GAME_LEVEL) {
            startLevel(currentLevel + 1)
        } else {
            finishGame()
        }
    }

    fun useHint() {
        if (isHintUsed) {
            Log.e("GameViewModel", "Hint already used")
            return
        }

        isHintUsed = true
        disclosedLetters += settings.rewardAmount

        clue = generateClue(drawing.word, disclosedLetters)
        gameEvents.onNext(ClueUpdateEvent(clue, drawing))
    }

    fun logAdRewardPrompt(adUnitId: String) {
        // COMPLETE: Log ad_reward_prompt event (101)
        analytics.logAdRewardPrompt(adUnitId)
    }

    fun logAdRewardImpression(adUnitId: String) {
        // COMPLETE: Log ad_reward_impression event (101)
        analytics.logAdRewardImpression(adUnitId)
    }

    private fun applyDifficulty() {
        disclosedLettersByDefault = when (settings.difficulty) {
            GameSettings.DIFFICULTY_EASY -> 2
            GameSettings.DIFFICULTY_NORMAL -> 1
            else -> 1
        }
        disclosedLetters = disclosedLettersByDefault
    }

    private fun requestNewDrawing() {
        disposeIfNeeded(drawingRequestDisposable)

        drawingRequestDisposable = drawingDao.getRandomDrawings(seenWords)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { d ->
                clue = generateClue(d.word, disclosedLetters)
                seenWords.add(d.word)
                drawing = d

                // COMPLETE: Log level_start event (101)
                analytics.logLevelStart(d.word)

                gameEvents.onNext(NewLevelEvent(currentLevel, clue, d))
            }
    }

    private fun startLevel(newLevel: Int) {
        numAttempts = 0
        isHintUsed = false
        currentLevel = newLevel
        levelStartTimeInMillis = System.currentTimeMillis()

        applyDifficulty()
        requestNewDrawing()
    }

    private fun finishGame() {
        // COMPLETE: Log game_complete event (101)
        analytics.logGameComplete(numCorrectAnswers)

        gameEvents.onNext(GameOverEvent(numCorrectAnswers))
    }

    private fun disposeIfNeeded(d: Disposable?) {
        if (null != d && !d.isDisposed) {
            d.dispose()
        }
    }

    override fun onCleared() {
        disposeIfNeeded(drawingRequestDisposable)
        disposeIfNeeded(gameEventDisposable)
    }
}
