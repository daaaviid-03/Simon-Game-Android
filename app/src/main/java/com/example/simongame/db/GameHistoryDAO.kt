package com.example.simongame2.dbimplementation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameHistoryDAO {
    @Insert
    fun insertGame(game: GameHistory)

    @Query("SELECT * FROM GameHistory WHERE difficultyLevel = :difficultyLevel ORDER BY duration DESC LIMIT 10")
    fun getTop10LongestGamesInDifficulty(difficultyLevel: Int): List<GameHistory>
}