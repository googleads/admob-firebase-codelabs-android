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
import com.google.codelab.awesomedrawingquiz.data.Drawing
import com.google.codelab.awesomedrawingquiz.data.DrawingDao
import com.google.codelab.awesomedrawingquiz.ui.game.GameSettings.Companion.MAX_GAME_LEVEL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.ArrayList

class GameViewModel(
    private val drawingDao: DrawingDao,
    private val settings: GameSettings
) : ViewModel() {

  private var drawingRequestDisposable: Disposable? = null

  private var gameEventDisposable: Disposable? = null

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
    disposeIfNeeded(gameEventDisposable)

    gameEventDisposable = gameEvents
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(listener)
  }

  fun startLevel() {
    numCorrectAnswers = 0
    seenWords.clear()

    startLevel(1)
  }

  fun checkAnswer(userAnswer: String) {
    numAttempts++

    val correct = drawing.word.equals(userAnswer, true)
    if (correct) {
      numCorrectAnswers++
      val elapsedTimeInSeconds =
          (System.currentTimeMillis() - levelStartTimeInMillis).toInt() / 1000

      gameEvents.onNext(
          LevelClearEvent(
              numAttempts, elapsedTimeInSeconds,
              currentLevel == GameSettings.MAX_GAME_LEVEL, isHintUsed, drawing
          )
      )
    } else {
      gameEvents.onNext(WrongAnswerEvent(drawing))
    }
  }

  fun skipLevel() {
    val elapsedTimeInSeconds =
        (System.currentTimeMillis() - levelStartTimeInMillis).toInt() / 1000
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
