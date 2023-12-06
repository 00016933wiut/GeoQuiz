package com.example.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class CheatActivityViewModel(private val cheatSavedStateHandle: SavedStateHandle) : ViewModel() {
    fun getAnswerShown():Boolean {
        return cheatSavedStateHandle[EXTRA_ANSWER_IS_SHOWN] ?: false
    }

    fun setAnswerShown(isAnswerShown: Boolean) {
        cheatSavedStateHandle[EXTRA_ANSWER_IS_SHOWN] = isAnswerShown
    }
}