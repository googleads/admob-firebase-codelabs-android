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

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

// COMPLETE: Add FirebaseRemoteConfig as a class member (102)
class GameSettings(private val rc: FirebaseRemoteConfig) {

    val difficulty: String
        get() = DIFFICULTY_NORMAL

    // COMPLETE: Apply reward amount from the Remote Config (102)
    val rewardAmount: Int
        get() = rc.getLong(KEY_REWARD_AMOUNT).toInt()

    companion object {

        const val MAX_GAME_LEVEL = 6

        const val DIFFICULTY_EASY = "easy"

        const val DIFFICULTY_NORMAL = "normal"

        // COMPLETE: Add a key for 'reward_amount' Remote Config parameter (102)
        private const val KEY_REWARD_AMOUNT = "reward_amount"

    }
}


