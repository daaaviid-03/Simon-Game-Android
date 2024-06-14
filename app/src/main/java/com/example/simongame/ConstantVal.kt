package com.example.simongame

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SimonColorRed = Color.Red
val SimonColorBlue = Color.Blue
val SimonColorGreen = Color.Green
val SimonColorYellow = Color.Yellow

val SIMON_COLOR_LIST = listOf(SimonColorRed, SimonColorBlue, SimonColorGreen, SimonColorYellow)

val SimonButtonShapeIcons = listOf(R.drawable.scuare, R.drawable.circle, R.drawable.hexagon, R.drawable.heart, R.drawable.star)

val SimonButtonSound = listOf(R.raw.do_note, R.raw.re_note, R.raw.mi_note, R.raw.fa_note)

const val NUMBER_OF_LEVELS: Int = 5
val LEVEL_NAME = listOf("beginner", "easy", "intermediate", "difficult", "expert")
val LEVEL_LEN_INITIAL_SEQUENCE_STEPS = intArrayOf(2, 4, 6, 8, 10)
val LEVEL_VELOCITY_SEC = floatArrayOf(2f, 1.5f, 1f, 0.75f, 0.5f)
val LEVEL_MAX_RESPONSE_TIME_SEC = floatArrayOf(10f, 5f, 4f, 3f, 2f) // Float.POSITIVE_INFINITY,

const val BOARD_SHAPE_INDEX_KEY = "image_index"
const val MUSIC_LEVEL_KEY = "music_level"
const val SOUND_LEVEL_KEY = "sound_level"

const val ConfirmExitDialogInformationSettings = "Are you sure you want to exit without saving the changes?\n\nChanged settings won't be saved."
const val ConfirmExitDialogInformationGame = "Are you sure you want to exit the game?\n\nYour progress will be lost."
const val ConfirmExitDialogInformationEndGame = "Are you sure you want to exit the game without saving the record?\n\nThe record will be lost forever."

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
    builder.setPositiveButton("Exit") { _, _ ->
        exitThisActivity(context)
    }
    builder.setNegativeButton("Cancel Exit") { dialog, _ ->
        extraFunction()
        dialog.cancel()
    }
    val dialog = builder.create()
    dialog.show()
}

fun exitThisActivity(context: Context) {
    (context as Activity).finish()
}

@Composable
fun UpperBarControl(context: Context, title: String, exitAction: () -> Unit = {exitThisActivity(context)}) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Button(
            onClick = {
                exitAction()
            },
            modifier = Modifier.padding(16.dp)
        )
        {
            Text(text = "◀")
        }
        Text(title, fontSize = 40.sp, modifier = Modifier.padding(16.dp))
    }
}





