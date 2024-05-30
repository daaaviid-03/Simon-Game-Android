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
import com.example.simongame.ConstantVal

class GameActivity : ComponentActivity() {
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
                    confirmExit(this@GameActivity)
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
                    BackgroundLayout(context)
                }
            }
        }
    }
}

@Composable
fun BackgroundLayout(context: Context){
    var difficulty by rememberSaveable { mutableIntStateOf(0) }
    var state by rememberSaveable { mutableIntStateOf(GameActivity.state) }
    if (state == 0){
        DifficultySelectorActivity(context) {
            difficulty = it
            GameActivity.state = 1
            state = 1
        }
    } else {
        PlayGameActivity(context) {
            val sequenceLen = it
        }
    }
}

@Composable
fun DifficultySelectorActivity(context: Context, onclick: (difficulty: Int) -> Unit){

    var difficulty by rememberSaveable { mutableIntStateOf(1) }

    val difficultyRange = 1f..ConstantVal.NUMBER_OF_LEVELS.toFloat()
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
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
        //verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Select\n\n difficulty: $difficulty",
            fontSize = 60.sp,
            modifier = Modifier.weight(2f)
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Slider(
            value = difficulty.toFloat(),
            onValueChange = {
                difficulty = it.toInt()
            },
            valueRange = difficultyRange,
            steps = ConstantVal.NUMBER_OF_LEVELS - 1,

            modifier = Modifier.size(width = 300.dp, height = 150.dp)
                .weight(1f)
        )
        Spacer(modifier = Modifier.weight(0.5f))
        InfoLineLayout(
            infoText="Initial sequence length:",
            value=ConstantVal.LEVEL_NAME[difficulty - 1],
            valUnit=""
        )
        InfoLineLayout(
            infoText="Showing velocity:",
            value=ConstantVal.LEVEL_VELOCITY_SEC[difficulty - 1].toString(),
            valUnit="s/c"
        )
        InfoLineLayout(
            infoText="Time to respond:",
            value=ConstantVal.LEVEL_MAX_RESPONSE_TIME_SEC[difficulty - 1].toString(),
            valUnit="s/c"
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Button(
            onClick = {
                onclick(difficulty)
            },
            modifier = Modifier.size(width = 300.dp, height = 150.dp)
                .weight(2f)
        )
        {
            Text("PLAY NOW")
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
fun InfoLineLayout(infoText: String, value: String, valUnit: String){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
        //horizontalArrangement = Arrangement.spacedBy(30.dp)
    ){
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            text = infoText,
            fontSize = 20.sp,
            modifier = Modifier.weight(3f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$value $valUnit",
            fontSize = 20.sp,
            modifier = Modifier.weight(2f)
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }
    Spacer(modifier = Modifier.height(8.dp))
}

fun confirmExit(context: Context){
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

@Composable
fun PlayGameActivity(context: Context, onclick: (sequenceLen: Int) -> Unit){

    Box(
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp)
    ) {
        Button(onClick = {
            confirmExit(context)
        }) {
            Text(text = "◀")
        }
    }
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically)
    ) {

    }
}