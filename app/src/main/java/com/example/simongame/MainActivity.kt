package com.example.simongame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.simongame.game.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.simongame.otherwin.HighScoresActivity
import com.example.simongame.otherwin.SettingsActivity
import com.example.simongame.ui.theme.SimonGameTheme

/**
 * Main Menu Activity
 */
class MainActivity : ComponentActivity() {
    companion object {
        /**
         * Shared Preferences object for storing game settings
         */
        lateinit var sharedPreferences: SharedPreferences
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("SimonGame_Preferences", Context.MODE_PRIVATE)
        MusicManager.startMusic(this, sharedPreferences.getInt(MUSIC_LEVEL_KEY, 100) / 100f)
        setContent {
            val context = LocalContext.current

            SimonGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainMenuLayout(context)
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        MusicManager.stopMusic()
    }

    override fun onPause() {
        super.onPause()
        MusicManager.pauseMusic()
    }
    override fun onResume() {
        super.onResume()
        MusicManager.resumeMusic()
    }

}

/**
 * Main Menu Layout
 */
@Composable
fun MainMenuLayout(context: Context) {
    val colors = SIMON_COLOR_LIST
    var currentIndex by remember { mutableIntStateOf(0) }

    // Change color of button every 3 seconds
    LaunchedEffect(Unit) {
        while (true) {
            currentIndex = (currentIndex + 1) % colors.size
            delay(3000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        // Display the profile picture
        Icon(
            Icons.Rounded.Person,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .aspectRatio(1f),
        )
        Spacer(modifier = Modifier.height(50.dp))
        // Display the settings button
        Button(
            modifier = Modifier.size(width = 250.dp, height = 50.dp),
            onClick = {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(context, intent, null)
            }
        ) {
            Text("Settings", fontSize = 25.sp)
        }
        Spacer(modifier = Modifier.height(30.dp))
        // Display the high scores button
        Button(
            modifier = Modifier.size(width = 250.dp, height = 50.dp),
            onClick = {
                val intent = Intent(context, HighScoresActivity::class.java)
                startActivity(context, intent, null)
            }
        ) {
            Text(
                text = "High Scores",
                fontSize = 25.sp
            )
        }
        Spacer(modifier = Modifier.height(150.dp))
        // Display the play button
        Button(
            onClick = {
                val intent = Intent(context, GameActivity::class.java)
                startActivity(context, intent, null)
            },
            modifier = Modifier
                .size(width = 300.dp, height = 150.dp),
            colors = ButtonDefaults.buttonColors(colors[currentIndex]),
        )
        {
            Text("PLAY NOW", fontSize = 40.sp, color = invertColor(colors[currentIndex]))
        }
    }
}
