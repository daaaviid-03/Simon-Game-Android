package com.example.simongame.db

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class HistoryRepository(private val dao: GameHistoryDAO) {

    fun insertGame(userName: String, difficultyLevel: Int, duration: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val thisHist =
                GameHistory(0, userName, Instant.now().toEpochMilli(), difficultyLevel, duration)
            dao.insertGame(thisHist)
        }
    }

    fun getTop10(difficultyLevel: Int): List<GameHistory> {
        var thisHist = listOf<GameHistory>()
        CoroutineScope(Dispatchers.IO).launch {
            thisHist = dao.getTop10LongestGamesInDifficulty(difficultyLevel)
        }
        return thisHist
    }

    fun getLast5(): List<String> {
        var thisNames = listOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            thisNames = dao.getLast5NamesFromRecords()
        }
        return thisNames
    }

}