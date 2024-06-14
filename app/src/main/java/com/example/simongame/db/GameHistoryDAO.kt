package com.example.simongame.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameHistoryDAO {
    @Insert
    fun insertGame(game: GameHistory)

    @Query("SELECT * FROM GameHistory WHERE difficultyLevel = :difficultyLevelV ORDER BY duration DESC LIMIT 10")
    fun getTop10LongestGamesInDifficulty(difficultyLevelV: Int): List<GameHistory>

    @Query("SELECT DISTINCT userName FROM GameHistory ORDER BY date DESC LIMIT 5")
    fun getLast5NamesFromRecords(): List<String>
}