package com.example.geoquiz

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private val cheatViewModel: CheatActivityViewModel by viewModels()
    private var score = 0
    private var answered: HashMap<Int, Int> = HashMap()
    private val cheatLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isCheater =
                    result.data?.getBooleanExtra(EXTRA_ANSWER_IS_SHOWN, false) ?: false
                cheatViewModel.setAnswerShown(isCheater)
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton(){
        val effect = RenderEffect.createBlurEffect(10.0f, 10.0f, Shader.TileMode.CLAMP)
        binding.cheatButton.setRenderEffect(effect)
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionText.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            cheatViewModel.getAnswerShown() -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_answer
            else -> R.string.wrong_answer
        }
        val message = getString(messageResId)
        Snackbar.make(
            binding.root,
            "$message       $score / ${quizViewModel.sizeOfQuestionBank}",
            2000
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateQuestion()
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.S){
            blurCheatButton()
        }

        binding.btnTrue.setOnClickListener { _: View ->
            if (answered.containsKey(quizViewModel.getCurentIndex())) {
                Snackbar.make(binding.root, "You already answered", 2000).show()
            } else {
                answered[quizViewModel.getCurentIndex()] =
                    if (quizViewModel.currentQuestionAnswer) 1 else 0
                score =
                    if (quizViewModel.currentQuestionAnswer and !cheatViewModel.getAnswerShown()) score + 1 else score
                checkAnswer(true)
            }
        }
        binding.btnFalse.setOnClickListener { _: View ->
            if (answered.containsKey(quizViewModel.getCurentIndex())) {
                Snackbar.make(binding.root, "You already answered", 2000).show()
            } else {
                answered[quizViewModel.getCurentIndex()] =
                    if (!quizViewModel.isCheater) 1 else 0
                score =
                    if (!quizViewModel.currentQuestionAnswer and !cheatViewModel.getAnswerShown()) score + 1 else score
                checkAnswer(false)
            }
        }
        binding.btnNext.setOnClickListener { _: View ->
            quizViewModel.nextQuestion()
            updateQuestion()
            binding.questionNumber.text = "${quizViewModel.getCurentIndex()}/${quizViewModel.sizeOfQuestionBank}"
            if(quizViewModel.getCurentIndex()+1==quizViewModel.sizeOfQuestionBank){
                binding.btnNext.setText(R.string.finish_text)
                binding.btnNext.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#4EAA00"))
            }

        }
        binding.btnPrev.setOnClickListener { _: View ->
            quizViewModel.previousQuestion()
            updateQuestion()
            binding.questionNumber.text = "${quizViewModel.getCurentIndex()}/${quizViewModel.sizeOfQuestionBank}"
            if(quizViewModel.getCurentIndex()!=quizViewModel.sizeOfQuestionBank){
                binding.btnNext.setText(R.string.nextBtn)
                binding.btnNext.backgroundTintList= ColorStateList.valueOf(Color.rgb(80,60,130))
            }
        }
        binding.cheatButton.setOnClickListener { _: View ->

            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)

        }
    }
}