package com.example.simongame.game

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.simongame.ui.theme.SimonGameTheme
import android.app.Activity
import android.app.AlertDialog
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.ui.unit.sp

class HighScores : ComponentActivity() {
    companion object {
        var state: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (state == 0){
                    finish()
                } else {
                    confirmExit1(this@HighScores)
                }
            }
        })


        setContent {
            val context = LocalContext.current

            //val db = UsersDB.getInstance(context)
            //val usersRep = UserRepository(db.userDao())

            //val sharedPreferences = getSharedPreferences("app_data", Context.MODE_PRIVATE)
            //sharedPreferences.edit().putInt("my_int_key", myIntValue).apply()
            //val lastUserUsedId = sharedPreferences.getInt("last_user_id", -1)
            //var thisUser by rememberSaveable {
            //    mutableStateOf<User?>(null)
            //}
            //if (lastUserUsedId != -1) thisUser = usersRep.getUser(lastUserUsedId)
            SimonGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color =  MaterialTheme.colorScheme.background
                ) {
                    state = 0
                    BackgroundLayout1(context)
                }
            }
        }
    }
}

@Composable
fun BackgroundLayout1(context: Context){
    Box(
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp)
    ) {
        Button(onClick = {
            (context as Activity).finish()
        }) {
            Text(text = "◀")
        }
    }
}

fun confirmExit1(context: Context){
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Confirm Action")
    builder.setMessage("Are you sure you want to exit the game?\n\nAll game progress will be lost.")

    builder.setPositiveButton("Exit") { dialog, which ->
        (context as Activity).finish()
    }

    builder.setNegativeButton("Cancel Exit") { dialog, which ->
        dialog.cancel()
    }

    val dialog = builder.create()
    dialog.show()
}

