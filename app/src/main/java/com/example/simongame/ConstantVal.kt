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

/**
 * Reference to the red color to standardise the colors in the app
 */
val SimonColorRed = Color.Red

/**
 * Reference to the blue color to standardise the colors in the app
 */
val SimonColorBlue = Color.Blue

/**
 * Reference to the green color to standardise the colors in the app
 */
val SimonColorGreen = Color.Green

/**
 * Reference to the yellow color to standardise the colors in the app
 */
val SimonColorYellow = Color.Yellow

/**
 * List of all the colors in the app
 */
val SIMON_COLOR_LIST = listOf(SimonColorRed, SimonColorBlue, SimonColorGreen, SimonColorYellow)

/**
 * List of all the possible shape buttons in the app
 */
val SimonButtonShapeIcons = listOf(R.drawable.scuare, R.drawable.circle, R.drawable.hexagon, R.drawable.heart, R.drawable.star)

/**
 * List of the sound for each button in the game
 */
val SimonButtonSound = listOf(R.raw.do_note, R.raw.re_note, R.raw.mi_note, R.raw.fa_note)

/**
 * Number of levels in the game
 */
const val NUMBER_OF_LEVELS: Int = 5

/**
 * List of the names of all the levels in the game
 */
val LEVEL_NAME = listOf("beginner", "easy", "intermediate", "difficult", "expert")

/**
 * List of the initial sequence steps for each level
 */
val LEVEL_LEN_INITIAL_SEQUENCE_STEPS = intArrayOf(2, 4, 6, 8, 10)

/**
 * List of the velocity of the steps for each level
 */
val LEVEL_VELOCITY_SEC = floatArrayOf(2f, 1.5f, 1f, 0.75f, 0.5f)

/**
 * List of the maximum response time for each level
 */
val LEVEL_MAX_RESPONSE_TIME_SEC = floatArrayOf(10f, 5f, 4f, 3f, 2f) // Float.POSITIVE_INFINITY,

/**
 * Name for the shared preferences file of the board shape value
 */
const val BOARD_SHAPE_INDEX_KEY = "image_index"

/**
 * Name for the shared preferences file of the music level value
 */
const val MUSIC_LEVEL_KEY = "music_level"

/**
 * Name for the shared preferences file of the sound level value
 */
const val SOUND_LEVEL_KEY = "sound_level"

/**
 * String of the confirm exit dialog information for the settings
 */
const val ConfirmExitDialogInformationSettings = "Are you sure you want to exit without saving the changes?\n\nChanged settings won't be saved."

/**
 * String of the confirm exit dialog information for the game
 */
const val ConfirmExitDialogInformationGame = "Are you sure you want to exit the game?\n\nYour progress will be lost."

/**
 * String of the confirm exit dialog information for the end game
 */
const val ConfirmExitDialogInformationEndGame = "Are you sure you want to exit the game without saving the record?\n\nThe record will be lost forever."

/**
 * Function to invert the color of a color
 */
fun invertColor(color: Color): Color {
    val red = 1 - color.red
    val green = 1 - color.green
    val blue = 1 - color.blue
    return Color(red, green, blue)
}

/**
 * Function to show a dialog to confirm the exit of the app
 */
fun confirmExitDialog(context: Context, information: String, extraFunctionToCancel: () -> Unit = {}, extraFunctionToExit: () -> Unit = {}) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Confirm Action")
    builder.setMessage(information)
    builder.setPositiveButton("Exit") { _, _ ->
        extraFunctionToExit()
        exitThisActivity(context)
    }
    builder.setNegativeButton("Cancel Exit") { dialog, _ ->
        extraFunctionToCancel()
        dialog.cancel()
    }
    val dialog = builder.create()
    dialog.show()
}

/**
 * Function to exit the actual activity
 */
fun exitThisActivity(context: Context) {
    (context as Activity).finish()
}

/**
 * Function to display the upper bar of the actual activity
 */
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





