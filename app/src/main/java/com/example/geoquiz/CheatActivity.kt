package com.example.geoquiz
 
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.geoquiz.databinding.ActivityCheatBinding

const val EXTRA_ANSWER_IS_SHOWN = "com.example.android.geoquiz.answer_is_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.example.android.geoquiz.answer_is_true"
private var answerIsTrue = false

class CheatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheatBinding
    private val cheatViewModel: CheatActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        binding.showAnswerButton.setOnClickListener { _: View ->
            val answerText = when {
                answerIsTrue -> R.string.trueBtnName
                else -> R.string.falseBtnName
            }
            cheatViewModel.setAnswerShown(true)
            binding.answerTextView.setText(answerText)
            val data = Intent().apply { putExtra(EXTRA_ANSWER_IS_SHOWN, true) }
            setResult(Activity.RESULT_OK, data)
        }
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}