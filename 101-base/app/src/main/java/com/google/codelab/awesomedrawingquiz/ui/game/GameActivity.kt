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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.codelab.awesomedrawingquiz.AwesomeDrawingQuiz
import com.google.codelab.awesomedrawingquiz.R
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.content_full_screen_dialog.*

class GameActivity : AppCompatActivity(), RewardedVideoAdListener {

  private lateinit var etAnswer: EditText

  private lateinit var dlgAnswer: AlertDialog

  private val viewModel by lazy {
    ViewModelProviders.of(
        this, (application as AwesomeDrawingQuiz).provideViewModelFactory()
    )[GameViewModel::class.java]
  }

  private val rewardedVideo by lazy {
    MobileAds.getRewardedVideoAdInstance(this).apply {
      rewardedVideoAdListener = this@GameActivity
    }
  }

  // TODO: Get a reference to FirebaseAnalytics instance (101)


  private val isHintAvailable: Boolean
    get() = viewModel.isHintAvailable && rewardedVideo.isLoaded

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_game)

    loadRewardedVideoAd()

    setupViews()
    setupDialog()
    setupViewModel()

    showGameStartFullScreenDialog()
    // TODO: Log game_start event (101)

  }

  override fun onPause() {
    super.onPause()
    rewardedVideo.pause(this)
  }

  override fun onResume() {
    super.onResume()
    rewardedVideo.resume(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    rewardedVideo.destroy(this)
  }

  private fun setupViews() {
    btnActivityGameHint.setOnClickListener { showHintConfirmDialog() }
    btnActivityGameAnswer.setOnClickListener { showAnswerInputDialog() }
    btnActivityGameSkip.setOnClickListener { viewModel.skipLevel() }
  }

  @SuppressLint("InflateParams")
  private fun setupDialog() {
    val dialogView = LayoutInflater.from(this)
        .inflate(R.layout.dialog_answer, null, false)

    etAnswer = dialogView.findViewById(R.id.et_dialog_answer)

    dlgAnswer = AlertDialog.Builder(this)
        .setTitle(R.string.enter_your_answer)
        .setView(dialogView)
        .setPositiveButton(android.R.string.ok) { _, _ ->
          viewModel.checkAnswer(etAnswer.text.toString())
        }.setNegativeButton(android.R.string.cancel, null)
        .create()
  }

  private fun setupViewModel() {
    viewModel.registerGameEventListener {
      when (it) {
        is NewLevelEvent -> {
          Log.d(TAG, "Round loaded: $it")
          // TODO: Log level_start event (101)


          tvActivityGameRound.text = getString(
              R.string.level_indicator, it.currentLevel, GameSettings.MAX_GAME_LEVEL
          )
          tvActivityGameHint.text = it.clue
          etAnswer.hint = it.clue

          qvActivityGame.draw(it.drawing)
          updateHintAvailability()
        }

        is ClueUpdateEvent -> {
          Log.d(TAG, "Clue updated: ${it.newClue}")

          tvActivityGameHint.text = it.newClue
          etAnswer.hint = it.newClue

          updateHintAvailability()
        }

        is WrongAnswerEvent -> {
          Log.d(TAG, "Wrong Answer: $it")
          // TODO: Log level_wrong_answer event (101)


          showWrongAnswerFullScreenDialog()
        }

        is LevelClearEvent -> {
          Log.d(TAG, "Round cleared: $it")
          // TODO: Log level_success event (101)


          if (it.isFinalLevel) {
            viewModel.moveToNextLevel()
          } else {
            showLevelClearFullScreenDialog()
          }
        }

        is LevelSkipEvent -> {
          Log.d(TAG, "Round skipped: $it")
          // TODO: Log level_fail event (101)


          if (!rewardedVideo.isLoaded) {
            loadRewardedVideoAd()
          }
        }

        is GameOverEvent -> {
          Log.d(TAG, "Game over: $it")
          // TODO: Log game_complete event (101)


          showGameOverFullScreenDialog(it.numCorrectAnswers)
        }
      }
    }
  }

  private fun showAnswerInputDialog() {
    etAnswer.setText("")
    dlgAnswer.show()
  }

  private fun showGameStartFullScreenDialog() {
    tvActivityGameFullScreenDialogTitle.setText(R.string.guess_the_name)
    tvActivityGameFullScreenDialogMessage.visibility = View.GONE
    with(btnActivityGameFullScreenDialogAction) {
      setText(R.string.get_started)
      setOnClickListener {
        hideFullScreenDialog()
        viewModel.startLevel()
      }
    }

    flActivityGameFullScreenDialog
        .animate()
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationStart(animation: Animator) {
            flActivityGameFullScreenDialog.visibility = View.VISIBLE
            super.onAnimationStart(animation)
          }
        })
  }

  private fun showWrongAnswerFullScreenDialog() {
    tvActivityGameFullScreenDialogTitle.setText(R.string.wrong_answer)
    tvActivityGameFullScreenDialogMessage.visibility = View.GONE
    with(btnActivityGameFullScreenDialogAction) {
      setText(R.string.try_again)
      setOnClickListener { hideFullScreenDialog() }
    }

    flActivityGameFullScreenDialog
        .animate()
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationStart(animation: Animator) {
            flActivityGameFullScreenDialog.visibility = View.VISIBLE
            super.onAnimationStart(animation)
          }
        })
  }

  private fun showLevelClearFullScreenDialog() {
    tvActivityGameFullScreenDialogTitle.setText(R.string.good_job)
    tvActivityGameFullScreenDialogMessage.visibility = View.GONE
    with(btnActivityGameFullScreenDialogAction) {
      setText(R.string.next_level)
      setOnClickListener {
        hideFullScreenDialog()
        viewModel.moveToNextLevel()
      }
    }

    flActivityGameFullScreenDialog
        .animate()
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationStart(animation: Animator) {
            flActivityGameFullScreenDialog.visibility = View.VISIBLE
            super.onAnimationStart(animation)
          }
        })
  }

  private fun showGameOverFullScreenDialog(numberOfCorrectAnswers: Int) {
    tvActivityGameFullScreenDialogTitle.setText(R.string.game_over)
    with(tvActivityGameFullScreenDialogMessage) {
      visibility = View.VISIBLE
      text = getString(R.string.correct_answers, numberOfCorrectAnswers)
    }
    with(btnActivityGameFullScreenDialogAction) {
      setText(R.string.back_to_main_menu)
      setOnClickListener { finish() }
    }

    flActivityGameFullScreenDialog
        .animate()
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationStart(animation: Animator) {
            flActivityGameFullScreenDialog.visibility = View.VISIBLE
            super.onAnimationStart(animation)
          }
        })
  }

  private fun hideFullScreenDialog() {
    flActivityGameFullScreenDialog.animate().alpha(0f)
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            flActivityGameFullScreenDialog.visibility = View.GONE
          }
        })
  }

  private fun showHintConfirmDialog() {
    // TODO: Log ad_reward_prompt event (101)


    AlertDialog.Builder(this)
        .setTitle(R.string.need_a_hint)
        .setMessage(R.string.need_a_hint_description)
        .setPositiveButton(android.R.string.ok) { _, _ -> showRewardedVideoAd() }
        .setNegativeButton(android.R.string.no, null)
        .show()
  }

  private fun updateHintAvailability() {
    btnActivityGameHint.isEnabled = isHintAvailable
  }

  private fun loadRewardedVideoAd() {
    Log.d(TAG, "Requesting rewarded video ad")
    rewardedVideo.loadAd(
        AD_UNIT_ID, AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build()
    )
  }

  private fun showRewardedVideoAd() {
    if (rewardedVideo.isLoaded) {
      rewardedVideo.show()
    } else {
      Log.e(TAG, "Rewarded Video Ad was not loaded yet")
    }
  }

  override fun onRewardedVideoAdLoaded() {
    Log.d(TAG, "onRewardedVideoAdLoaded()")
    updateHintAvailability()
  }

  override fun onRewardedVideoAdOpened() {
    Log.d(TAG, "onRewardedVideoAdOpened()")
  }

  override fun onRewardedVideoStarted() {
    Log.d(TAG, "onRewardedVideoStarted()")
    // TODO: Log ad_reward_impression event (101)

  }

  override fun onRewardedVideoAdClosed() {
    Log.d(TAG, "onRewardedVideoAdClosed()")
    btnActivityGameHint.isEnabled = false
    loadRewardedVideoAd()
  }

  override fun onRewarded(rewardItem: RewardItem) {
    Log.d(TAG, "onRewarded()")
    viewModel.useHint()
  }

  override fun onRewardedVideoAdLeftApplication() {
    Log.d(TAG, "onRewardedVideoAdLeftApplication()")
  }

  override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
    Log.d(TAG, "onRewardedVideoAdFailedToLoad() : $errorCode")
  }

  override fun onRewardedVideoCompleted() {
    Log.d(TAG, "onRewardedVideoCompleted()")
  }

  companion object {

    private const val TAG = "GameActivity"

    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/8673189370"
  }
}
