package com.example.simongame.game

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.media.AudioManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import android.content.SharedPreferences
import com.example.simongame.ui.theme.SimonGameTheme
import android.app.Activity
import android.app.AlertDialog
import androidx.compose.ui.Alignment
import androidx.compose.material3.Slider
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.simongame.BOARD_SHAPE_INDEX_KEY
import com.example.simongame.ConfirmExitDialogInformationSettings
import com.example.simongame.MUSIC_LEVEL_KEY
import com.example.simongame.SOUND_LEVEL_KEY
import com.example.simongame.SimonButtonShapeIcons
import com.example.simongame.SimonColorRed
import com.example.simongame.confirmExitDialog

class Settings : ComponentActivity() {

    companion object {
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("SimonGame_Preferences", Context.MODE_PRIVATE)
        setContent {
            val context = LocalContext.current
            SimonGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Background(context)
                }
            }
        }
    }
}

fun exitWithoutSaving(context: Context){
    (context as Activity).finish()
}

@Composable
fun Background(context: Context) {

    val boardShapes = SimonButtonShapeIcons

    val boardShapeIndex: MutableIntState = remember {
        mutableIntStateOf(
            Settings.sharedPreferences.getInt(BOARD_SHAPE_INDEX_KEY, 0)
        )
    }

    val currentMusicVolume: MutableIntState = remember {
        mutableIntStateOf(
            Settings.sharedPreferences.getInt(MUSIC_LEVEL_KEY, 100)
        )
    }

    val currentSoundVolume: MutableIntState = remember {
        mutableIntStateOf(
            Settings.sharedPreferences.getInt(SOUND_LEVEL_KEY, 100)
        )
    }

    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = {
                    confirmExitDialog(context, ConfirmExitDialogInformationSettings)
                },
                modifier = Modifier.padding(16.dp)
            )
            {
                Text(text = "◀")
            }
            Text("Settings", fontSize = 40.sp, modifier = Modifier.padding(16.dp))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Music:", fontSize = 25.sp)
            Slider(
                value = currentMusicVolume.intValue.toFloat(),
                onValueChange = {
                    currentMusicVolume.intValue = it.toInt()
                },
                valueRange = 0f..100f,
                steps = 101
            )
            Text("Sound:", fontSize = 25.sp)
            Slider(
                value = currentSoundVolume.intValue.toFloat(),
                onValueChange = {
                    currentSoundVolume.intValue = it.toInt()
                },
                valueRange = 0f..100f,
                steps = 101
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text("Board:", fontSize = 25.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        boardShapeIndex.intValue = (boardShapeIndex.intValue - 1) % boardShapes.size
                    }
                ) {
                    Text("◀")
                }
                Image(
                    painter = painterResource(id = boardShapes[boardShapeIndex.intValue]),
                    contentDescription = null,
                    modifier = Modifier.size(225.dp).padding(20.dp),
                    colorFilter = ColorFilter.tint(SimonColorRed)
                )
                Button(
                    onClick = {
                        boardShapeIndex.intValue = (boardShapeIndex.intValue + 1) % boardShapes.size
                    }
                ) {
                    Text("▶")
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
            Button(
                onClick = {
                    saveAndExit(context, boardShapeIndex.intValue, currentMusicVolume.intValue, currentSoundVolume.intValue)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save", fontSize = 40.sp)
            }
        }
    }
}

fun saveAndExit(context: Context, boardShapeIndex: Int, musicVolume: Int, soundVolume: Int){
    Settings.sharedPreferences.edit()
        .putInt(BOARD_SHAPE_INDEX_KEY, boardShapeIndex)
        .putInt(MUSIC_LEVEL_KEY, musicVolume)
        .putInt(SOUND_LEVEL_KEY, soundVolume)
        .apply()
    exitWithoutSaving(context)
}