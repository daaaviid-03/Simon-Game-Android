package com.example.simongame.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Game History Entity
 */
@Entity
data class GameHistory(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userName: String,
    val date: Long,
    val difficultyLevel: Int,
    val duration: Int
)