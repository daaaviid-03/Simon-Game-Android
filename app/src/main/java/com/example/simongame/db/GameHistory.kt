package com.example.simongame2.dbimplementation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameHistory(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userId: Int,
    val date: Long,
    val difficultyLevel: Int,
    val duration: Int
)