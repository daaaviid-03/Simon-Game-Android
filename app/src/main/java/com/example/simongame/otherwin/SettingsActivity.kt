package com.example.simongame.otherwin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import android.content.SharedPreferences
import com.example.simongame.ui.theme.SimonGameTheme
import androidx.compose.ui.Alignment
import androidx.compose.material3.Slider
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.sp
import com.example.simongame.BOARD_SHAPE_INDEX_KEY
import com.example.simongame.ConfirmExitDialogInformationSettings
import com.example.simongame.MUSIC_LEVEL_KEY
import com.example.simongame.MainActivity
import com.example.simongame.SOUND_LEVEL_KEY
import com.example.simongame.SimonButtonShapeIcons
import com.example.simongame.SimonColorRed
import com.example.simongame.UpperBarControl
import com.example.simongame.confirmExitDialog
import com.example.simongame.exitThisActivity

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Background(context)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MainActivity.ActivityCounter.count++
        if (MainActivity.ActivityCounter.count == 1) {
            MainActivity.MediaPlayerManager.mediaPlayer?.start()
            MainActivity.MediaPlayerManager.mediaPlayer?.isLooping
        }
    }
    override fun onPause() {
        super.onPause()
        MainActivity.ActivityCounter.count--
        if (MainActivity.ActivityCounter.count == 0) {
            MainActivity.MediaPlayerManager.mediaPlayer?.pause()
        }
    }
}

@Composable
fun Background(context: Context,) {
    val mediaPlayer = MainActivity.MediaPlayerManager.mediaPlayer
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
        UpperBarControl(context, "Settings") {
            confirmExitDialog(context, ConfirmExitDialogInformationSettings)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Music:", fontSize = 25.sp)
            Slider(
                value = currentMusicVolume.intValue.toFloat(),
                onValueChange = { newVolume ->
                    currentMusicVolume.intValue = newVolume.toInt()
                    val volumeFloat = newVolume / 100.0f
                    mediaPlayer?.setVolume(volumeFloat,volumeFloat)
                },
                valueRange = 0f..100f,
                steps = 100
            )
            Text("Sound:", fontSize = 25.sp)
            Slider(
                value = currentSoundVolume.intValue.toFloat(),
                onValueChange = {
                    currentSoundVolume.intValue = it.toInt()
                },
                valueRange = 0f..100f,
                steps = 100
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text("Board:", fontSize = 25.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        boardShapeIndex.intValue =
                            (boardShapeIndex.intValue - 1) % boardShapes.size
                    }
                ) {
                    Text("◀")
                }
                Image(
                    painter = painterResource(id = boardShapes[boardShapeIndex.intValue]),
                    contentDescription = null,
                    modifier = Modifier
                        .size(225.dp)
                        .padding(20.dp),
                    colorFilter = ColorFilter.tint(SimonColorRed)
                )
                Button(
                    onClick = {
                        boardShapeIndex.intValue =
                            (boardShapeIndex.intValue + 1) % boardShapes.size
                    }
                ) {
                    Text("▶")
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
            Button(
                onClick = {
                    saveAndExit(
                        context,
                        boardShapeIndex.intValue,
                        currentMusicVolume.intValue,
                        currentSoundVolume.intValue
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save", fontSize = 30.sp)
            }
        }
    }
}

private fun saveAndExit(context: Context, boardShapeIndex: Int, musicVolume: Int, soundVolume: Int) {
    Settings.sharedPreferences.edit()
        .putInt(BOARD_SHAPE_INDEX_KEY, boardShapeIndex)
        .putInt(MUSIC_LEVEL_KEY, musicVolume)
        .putInt(SOUND_LEVEL_KEY, soundVolume)
        .apply()
    exitThisActivity(context)
}