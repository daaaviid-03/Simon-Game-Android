package com.example.simongame2.dbimplementation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var image: String,
    var userName: String,
    var volumeLevel: Int
)