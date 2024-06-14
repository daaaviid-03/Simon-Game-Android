package com.example.simongame.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Game History DAO
 */
@Dao
interface GameHistoryDAO {
    /**
     * Insert a new game record into the database
     */
    @Insert
    fun insertGame(game: GameHistory)

    /**
     * Get the top 10 longest games in a specific difficulty level
     */
    @Query("SELECT * FROM GameHistory WHERE difficultyLevel = :difficultyLevelV ORDER BY duration DESC LIMIT 10")
    fun getTop10LongestGamesInDifficulty(difficultyLevelV: Int): List<GameHistory>

    /**
     * Get the last 5 user names used in game records
     */
    @Query("SELECT DISTINCT userName FROM GameHistory ORDER BY date DESC LIMIT 5")
    fun getLast5NamesFromRecords(): List<String>
}