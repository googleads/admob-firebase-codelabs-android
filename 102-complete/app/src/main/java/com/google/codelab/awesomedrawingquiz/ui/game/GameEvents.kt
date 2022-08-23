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

import androidx.annotation.IntRange
import com.google.codelab.awesomedrawingquiz.data.Drawing

sealed class GameEvent

data class LevelClearEvent(
    @IntRange(from = 1) val numAttempts: Int,
    @IntRange(from = 0) val elapsedTimeInSeconds: Int,
    val isFinalLevel: Boolean,
    val isHintUsed: Boolean,
    val drawing: Drawing
) : GameEvent()

data class ClueUpdateEvent(
    val newClue: String,
    val drawing: Drawing
) : GameEvent()

data class NewLevelEvent(
    @IntRange(from = 1, to = GameSettings.MAX_GAME_LEVEL.toLong()) val currentLevel: Int,
    val clue: String,
    val drawing: Drawing
) : GameEvent()

data class LevelSkipEvent(
    @IntRange(from = 1) val numAttempts: Int,
    @IntRange(from = 0) val elapsedTimeInSeconds: Int,
    val isHintUsed: Boolean,
    val drawing: Drawing
) : GameEvent()

data class GameOverEvent(
    @IntRange(from = 0, to = GameSettings.MAX_GAME_LEVEL.toLong()) val numCorrectAnswers: Int
) : GameEvent()

data class WrongAnswerEvent(
    val drawing: Drawing
) : GameEvent()
