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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.codelab.awesomedrawingquiz.AwesomeDrawingQuiz
import com.google.codelab.awesomedrawingquiz.R
import com.google.codelab.awesomedrawingquiz.databinding.ActivityGameBinding
import com.google.codelab.awesomedrawingquiz.databinding.DialogLevelBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var levelDialogBinding: DialogLevelBinding

    private var rewardedVideo: RewardedAd? = null

    private val viewModel by viewModels<GameViewModel> {
        (application as AwesomeDrawingQuiz).provideViewModelFactory()
    }

    private val isHintAvailable: Boolean
        get() = viewModel.isHintAvailable && (rewardedVideo?.isLoaded ?: false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        levelDialogBinding = DialogLevelBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupActions()
        setupViewModel()
        showGameStartFullScreenDialog()
    }

    private fun setupActions() {
        with(binding) {
            fabHint.setOnClickListener { showHintConfirmDialog() }
            bottomAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_skip -> {
                        viewModel.skipLevel()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel.registerGameEventListener {
            when (it) {
                is NewLevelEvent -> {

                    updateHintAvailability()
                }

                is ClueUpdateEvent -> {
                    updateHintAvailability()
                }

                is WrongAnswerEvent -> {
                    showWrongAnswerFullScreenDialog()
                }

                is LevelClearEvent -> {
                    if (it.isFinalLevel) {
                        binding.fabHint.visibility = View.INVISIBLE
                        viewModel.moveToNextLevel()
                    } else {
                        showLevelClearFullScreenDialog()
                    }
                }

                is LevelSkipEvent -> {
                    if (!rewardedVideo!!.isLoaded) {
                        loadRewardedVideoAd()
                    }
                }

                is GameOverEvent -> {
                    showGameOverFullScreenDialog(it.numCorrectAnswers)
                }
            }
        }
    }

    private fun showGameStartFullScreenDialog() {
        addDialogView()

        with(levelDialogBinding) {
            tvTitle.setText(R.string.guess_the_name)
            tvMessage.visibility = View.GONE
            with(btnAction) {
                setText(R.string.get_started)
                setOnClickListener {
                    removeDialogView()
                    viewModel.startGame()
                    loadRewardedVideoAd()
                }
            }
        }
    }

    private fun showWrongAnswerFullScreenDialog() {
        addDialogView()

        with(levelDialogBinding) {
            tvTitle.setText(R.string.wrong_answer)
            tvMessage.visibility = View.GONE
            with(btnAction) {
                setText(R.string.try_again)
                setOnClickListener { removeDialogView() }
            }
        }
    }

    private fun showLevelClearFullScreenDialog() {
        addDialogView()

        with(levelDialogBinding) {
            tvTitle.setText(R.string.good_job)
            tvMessage.visibility = View.GONE
            with(btnAction) {
                setText(R.string.next_level)
                setOnClickListener {
                    removeDialogView()
                    viewModel.moveToNextLevel()
                }
            }
        }
    }

    private fun showGameOverFullScreenDialog(numberOfCorrectAnswers: Int) {
        addDialogView()

        with(levelDialogBinding) {
            tvTitle.setText(R.string.game_over)
            with(tvMessage) {
                visibility = View.VISIBLE
                text = getString(R.string.correct_answers, numberOfCorrectAnswers)
            }
            with(btnAction) {
                setText(R.string.back_to_main_menu)
                setOnClickListener { finish() }
            }
        }
    }

    private fun addDialogView() {
        binding.root.addView(
            levelDialogBinding.root,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private fun removeDialogView() {
        binding.root.removeView(levelDialogBinding.root)
    }

    private fun showHintConfirmDialog() {
        viewModel.logAdRewardPrompt(AD_UNIT_ID)

        AlertDialog.Builder(this)
            .setTitle(R.string.need_a_hint)
            .setMessage(R.string.need_a_hint_description)
            .setPositiveButton(android.R.string.ok) { _, _ -> showRewardedVideoAd() }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    private fun updateHintAvailability() {
        if (isHintAvailable) {
            binding.fabHint.show()
        } else {
            binding.fabHint.hide()
        }
    }

    private fun loadRewardedVideoAd() {
        Log.d(TAG, "Requesting rewarded video ad")
        rewardedVideo = RewardedAd(this, AD_UNIT_ID)
        rewardedVideo?.loadAd(AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Log.d(TAG, "Rewarded Ad loaded")
                updateHintAvailability()
            }

            override fun onRewardedAdFailedToLoad(error: LoadAdError?) {
                Log.d(TAG, "Failed to load Rewarded ad: ${error?.message}")
            }
        })
    }

    private fun showRewardedVideoAd() {
        with(rewardedVideo!!) {
            if (isLoaded) {
                show(this@GameActivity, object : RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                        viewModel.logAdRewardImpression(AD_UNIT_ID)
                    }

                    override fun onUserEarnedReward(item: RewardItem) {
                        viewModel.useHint()
                    }
                })
            } else {
                Log.e(TAG, "Rewarded Video Ad was not loaded yet")
            }
        }
    }

    companion object {

        private const val TAG = "GameActivity"

        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/8673189370"
    }
}
