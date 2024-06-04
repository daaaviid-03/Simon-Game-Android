package com.example.simongame.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.random.Random

class GameViewModel(private var app: Application): AndroidViewModel(app) {

    var gameState = MutableLiveData(GameState.NotStarted)
    var sequence = MutableLiveData<MutableList<Int>>(mutableListOf())
    var buttonTriggers = MutableLiveData<MutableList<Boolean>>(mutableListOf())
    var buttonsDisabled = MutableLiveData(false)

    private var currentSequenceIndex = MutableLiveData(0)
    private var numberOfButtons: Int = 4

    fun startNewGame(initialSeqLength: Int, numberOfButtons: Int){
        this.numberOfButtons = numberOfButtons
        buttonTriggers.postValue(MutableList(numberOfButtons){false})
        buttonsDisabled.postValue(false)
        sequence.postValue(mutableListOf())
        resetCurrentSequenceIndex()
        for (i in 1..initialSeqLength){
            addToSequence()
        }
        postNewGameState(GameState.Showing)
    }

    private fun addToSequence(){
        val thisSequence = sequence.value!!
        thisSequence.add(Random.nextInt(numberOfButtons))
        sequence.postValue(thisSequence)
    }

    fun checkLastButtonPressed(buttonId: Int) {
        if (buttonId == sequence.value!![currentSequenceIndex.value!!]) {
            if (currentSequenceIndex.value!! >= sequence.value!!.size - 1) {
                nextLevel()
            } else {
                nextStep()
            }
        } else {
            gameOver()
        }
    }

    private fun gameOver(){
        postNewGameState(GameState.GameOver)
    }

    private fun nextLevel(){
        resetCurrentSequenceIndex()
        addToSequence()
        postNewGameState(GameState.Showing)
    }

    private fun nextStep(){
        nextCurrentSequenceIndex()
        postNewGameState(GameState.NextStep)
    }

    private fun resetCurrentSequenceIndex(){
        currentSequenceIndex.postValue(0)
    }

    private fun nextCurrentSequenceIndex(){
        currentSequenceIndex.postValue(currentSequenceIndex.value!! + 1)
    }

    fun postNewGameState(state: GameState) {
        gameState.postValue(state)
    }
}

@Suppress("UNCHECKED_CAST")
class GameViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(application) as T
    }
}



