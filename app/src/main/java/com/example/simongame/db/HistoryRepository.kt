package com.example.simongame2.dbimplementation

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class HistoryRepository(private val dao: GameHistoryDAO) {

    fun insertGame(userId: Int, difficultyLevel: Int, duration: Int) {
        val thisHist = GameHistory(0, userId, Instant.now().toEpochMilli(), difficultyLevel, duration)
        dao.insertGame(thisHist)
    }

    fun getTop10(difficultyLevel: Int): List<GameHistory> {
        return dao.getTop10LongestGamesInDifficulty(difficultyLevel)
    }

}