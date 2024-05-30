package com.example.simongame.game

import kotlin.random.Random


class Game {
    private val sequence: MutableList<Int> = mutableListOf()
    private var initialSeqLength: Int = 0
    private var numberOfButtons: Int = 0
    private var currentSequenceIndex = 0

    fun initiateGame(initialSeqLength: Int, numberOfButtons: Int){
        this.initialSeqLength = initialSeqLength
        this.numberOfButtons = numberOfButtons
    }

    fun startSequence(): MutableList<Int>{
        for (i in 1..initialSeqLength){
            addToSequence()
        }
        return getSequence()
    }

    fun checkInputButton(buttonNum: Int): Int{
        if (buttonNum == sequence[currentSequenceIndex++])
            if (currentSequenceIndex == sequence.size)
                return 2
            else
                return 1
        else
            return 0
    }

    private fun addToSequence(){
        sequence.add(Random.nextInt(numberOfButtons))
    }

    fun addToSequenceAndReturn(): Int{
        currentSequenceIndex = 0
        addToSequence()
        return sequence.last()
    }

    fun getSequence(): MutableList<Int>{
        return sequence
    }

    fun getScore(): Int{
        return sequence.size
    }

}



