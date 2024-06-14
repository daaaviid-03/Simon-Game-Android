package com.example.simongame.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.random.Random

/**
 * View model for the game mechanics
 */
class GameViewModel(app: Application): AndroidViewModel(app) {
    /**
     * Game state
     */
    var gameState = MutableLiveData(GameState.NotStarted)
    /**
     * Sequence of buttons to press
     */
    var sequence = MutableLiveData<MutableList<Int>>(mutableListOf())
    /**
     * Current index of the sequence
     */
    private var currentSequenceIndex = MutableLiveData(0)
    /**
     * Number of buttons in game
     */
    private var numberOfButtons: Int = 4

    /**
     * Start a new game
     */
    fun startNewGame(initialSeqLength: Int, numberOfButtons: Int){
        this.numberOfButtons = numberOfButtons
        sequence.postValue(mutableListOf())
        resetCurrentSequenceIndex()
        for (i in 1..initialSeqLength){
            addToSequence()
        }
        postNewGameState(GameState.Showing)
    }

    /**
     * Add a button to the sequence
     */
    private fun addToSequence(){
        val thisSequence = sequence.value!!
        thisSequence.add(Random.nextInt(numberOfButtons))
        sequence.postValue(thisSequence)
    }

    /**
     * Check if the last button pressed is correct
     */
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

    /**
     * Game over
     */
    private fun gameOver(){
        postNewGameState(GameState.GameOver)
    }

    /**
     * Next level of the game
     */
    private fun nextLevel(){
        resetCurrentSequenceIndex()
        addToSequence()
        postNewGameState(GameState.Showing)
    }

    /**
     * Next step of the game
     */
    private fun nextStep(){
        nextCurrentSequenceIndex()
        postNewGameState(GameState.NextStep)
    }

    /**
     * Reset the current sequence index
     */
    private fun resetCurrentSequenceIndex(){
        currentSequenceIndex.postValue(0)
    }

    /**
     * Next current sequence index
     */
    private fun nextCurrentSequenceIndex(){
        currentSequenceIndex.postValue(currentSequenceIndex.value!! + 1)
    }

    /**
     * Post a new game state
     */
    fun postNewGameState(state: GameState) {
        gameState.postValue(state)
    }
}

/**
 * Factory for the game view model
 */
@Suppress("UNCHECKED_CAST")
class GameViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(application) as T
    }
}



