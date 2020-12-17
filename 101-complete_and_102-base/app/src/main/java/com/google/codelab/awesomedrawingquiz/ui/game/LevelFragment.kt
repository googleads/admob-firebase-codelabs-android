package com.google.codelab.awesomedrawingquiz.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.codelab.awesomedrawingquiz.AwesomeDrawingQuiz
import com.google.codelab.awesomedrawingquiz.R
import com.google.codelab.awesomedrawingquiz.databinding.DialogAnswerBinding
import com.google.codelab.awesomedrawingquiz.databinding.FragmentLevelBinding

class LevelFragment : Fragment() {

    private var _binding: FragmentLevelBinding? = null

    private var _dialogBinding: DialogAnswerBinding? = null

    private val binding get() = _binding!!

    private val dialogBinding get() = _dialogBinding!!

    private val answerDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.enter_your_answer)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.checkAnswer(dialogBinding.etAnswer.text.toString())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    private val viewModel by activityViewModels<GameViewModel>() {
        (requireActivity().application as AwesomeDrawingQuiz).provideViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelBinding.inflate(inflater, container, false)
        _dialogBinding = DialogAnswerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAnswer.setOnClickListener {
            dialogBinding.etAnswer.setText("")
            answerDialog.show()
        }

        viewModel.registerGameEventListener {
            when (it) {
                is NewLevelEvent -> {
                    with (binding) {
                        tvLevel.text = getString(
                            R.string.level_indicator,
                            it.currentLevel,
                            GameSettings.MAX_GAME_LEVEL,
                        )
                        btnAnswer.text = it.clue
                        qvCanvas.draw(it.drawing)
                    }

                    dialogBinding.etAnswer.hint = it.clue
                }

                is ClueUpdateEvent -> {
                    binding.btnAnswer.text = it.newClue
                    dialogBinding.etAnswer.hint = it.newClue
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _dialogBinding = null
    }
}
