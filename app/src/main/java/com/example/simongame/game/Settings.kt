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
import com.example.simongame.game.*
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import android.app.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.ui.unit.sp
import com.example.simongame.R

class Settings : ComponentActivity() {
    private lateinit var audioManager: AudioManager
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val IMAGE_INDEX_KEY = "image_index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
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

    @Composable
    fun Background(context : Context) {
        var imageIndex by remember { mutableStateOf(sharedPreferences.getInt(IMAGE_INDEX_KEY, 0)) }
        var isChangesApplied by remember { mutableStateOf(false) }
        val imageList = listOf(R.drawable.board1, R.drawable.board2, R.drawable.board3)
        var showDialog by remember { mutableStateOf(false) }
        if (showDialog) {
            ConfirmExitDialog(
                onConfirm = {
                    showDialog = false
                    finish() // Cerrar la actividad si se confirman los cambios
                },
                onDismiss = {
                    showDialog = false // Solo cerrar el diálogo si se cancela la confirmación
                }
            )
        }
        val currentVolume =
            remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (isChangesApplied) {
                        (context as Activity).finish()
                    } else {
                        showDialog = true // Mostrar el diálogo de confirmación si hay cambios sin confirmar
                    }
                }
            )
            {
                Text(text = "◀")
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(100.dp))
            Text("Sound:", fontSize = 25.sp)
            Slider(
                value = currentVolume.value.toFloat(),
                onValueChange = { newValue ->
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newValue.toInt(), 0)
                    currentVolume.value = newValue.toInt()
                },
                valueRange = 0f..audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    .toFloat(),
                steps = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text("Board:", fontSize = 25.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    imageIndex = if (imageIndex > 0) imageIndex - 1 else imageList.size - 1
                    isChangesApplied = false // Indica que se han realizado cambios sin confirmar
                }) {
                    Text("<-")
                }
                Image(
                    painter = painterResource(id = imageList[imageIndex]),
                    contentDescription = null,
                    modifier = Modifier.size(225.dp)
                )
                Button(onClick = {
                    imageIndex = (imageIndex + 1) % imageList.size
                    isChangesApplied = false
                }
                ) {
                    Text("->")
                }
            }
                Spacer(modifier = Modifier.height(100.dp))
                Button(
                    onClick = {
                        sharedPreferences.edit()
                            .putInt(IMAGE_INDEX_KEY, imageIndex) // Guardar el índice de la imagen
                            .apply()
                        isChangesApplied = true
                        (context as Activity).finish()
                         },
                    enabled = !isChangesApplied // Deshabilitar el botón si no hay cambios sin confirmar
                ) {
                    Text("Confirmar cambios")
            }
        }
    }
    @Composable
    fun ConfirmExitDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Confirmar salida") },
            text = { Text("Tienes cambios sin confirmar. ¿Seguro que quieres salir?") },
            confirmButton = {
                Button(
                    onClick = { onConfirm() }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(
                    onClick = { onDismiss() }
                ) {
                    Text("No")
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}