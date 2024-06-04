package com.example.simongame

import androidx.compose.ui.graphics.Color

val SimonColorRed = Color.Red
val SimonColorBlue = Color.Blue
val SimonColorGreen = Color.Green
val SimonColorYellow = Color.Yellow

val SIMON_COLOR_LIST = listOf(SimonColorRed, SimonColorBlue, SimonColorGreen, SimonColorYellow)

const val NUMBER_OF_LEVELS: Int = 5
val LEVEL_NAME = listOf("beginner", "easy", "intermediate", "difficult", "expert")
val LEVEL_LEN_INITIAL_SEQUENCE_STEPS = intArrayOf(2, 4, 6, 8, 10)
val LEVEL_VELOCITY_SEC = floatArrayOf(2f, 1.5f, 1f, 0.75f, 0.5f)
val LEVEL_MAX_RESPONSE_TIME_SEC = floatArrayOf(10f, 5f, 4f, 3f, 2f) // Float.POSITIVE_INFINITY,
