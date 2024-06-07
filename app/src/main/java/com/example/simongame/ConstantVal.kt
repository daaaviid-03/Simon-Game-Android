package com.example.simongame

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.compose.ui.graphics.Color

val SimonColorRed = Color.Red
val SimonColorBlue = Color.Blue
val SimonColorGreen = Color.Green
val SimonColorYellow = Color.Yellow

val SIMON_COLOR_LIST = listOf(SimonColorRed, SimonColorBlue, SimonColorGreen, SimonColorYellow)

val SimonButtonShapeIcons = listOf(R.drawable.scuare, R.drawable.circle, R.drawable.hexagon, R.drawable.heart, R.drawable.star)

const val NUMBER_OF_LEVELS: Int = 5
val LEVEL_NAME = listOf("beginner", "easy", "intermediate", "difficult", "expert")
val LEVEL_LEN_INITIAL_SEQUENCE_STEPS = intArrayOf(2, 4, 6, 8, 10)
val LEVEL_VELOCITY_SEC = floatArrayOf(2f, 1.5f, 1f, 0.75f, 0.5f)
val LEVEL_MAX_RESPONSE_TIME_SEC = floatArrayOf(10f, 5f, 4f, 3f, 2f) // Float.POSITIVE_INFINITY,

val BOARD_SHAPE_INDEX_KEY = "image_index"
val MUSIC_LEVEL_KEY = "music_level"
val SOUND_LEVEL_KEY = "sound_level"

val ConfirmExitDialogInformationSettings = "Are you sure you want to exit without saving the changes?\n\nChanged settings won't be saved."
val ConfirmExitDialogInformationGame = "Are you sure you want to exit the game?\n\nYour progress will be lost."
val ConfirmExitDialogInformationEndGame = "Are you sure you want to exit the game without saving the record?\n\nThe record will be lost forever."

fun invertColor(color: Color): Color {
    val red = 1 - color.red
    val green = 1 - color.green
    val blue = 1 - color.blue
    return Color(red, green, blue)
}

fun confirmExitDialog(context: Context, information: String, extraFunction: () -> Unit = {}) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Confirm Action")
    builder.setMessage(information)
    builder.setPositiveButton("Exit") { dialog, which ->
        (context as Activity).finish()
    }
    builder.setNegativeButton("Cancel Exit") { dialog, which ->
        extraFunction()
        dialog.cancel()
    }
    val dialog = builder.create()
    dialog.show()
}





