package com.example.simongame

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Box
import androidx.core.content.ContextCompat.startActivity
import com.example.simongame.game.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.simongame.ui.theme.SimonGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    MainMenuLayout(context)
                }
            }
        }
    }
}

@Composable
fun MainMenuLayout(context: Context) {
        val colors = listOf(Color.Red, Color.Green, Color.Yellow, Color.Blue)
        var currentIndex by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            while (true) {
                currentIndex = (currentIndex + 1) % colors.size
                delay(1000) // Cambiar cada segundo
            }
        }
        //val userImage = if (thisUser != null)
        //    ImageBitmap.imageResource(R.drawable.ic_launcher_foreground)
        //else
        //    ImageBitmap.imageResource(R.drawable.ic_launcher_foreground)
        //val userName = thisUser?.userName ?: "unknown"
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.diamante),
                contentDescription = "diamante",
                modifier = Modifier
                    .fillMaxWidth(0.1f)
                    .aspectRatio(1f),
                alignment = Alignment.Center
            )
            Text(
                text = "cantidad ",
                color = Color.Black,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Image(
            //    modifier = Modifier.size(100.dp),
            //    contentDescription = "UsersImage",
            //    bitmap = userImage
            //)
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = R.drawable.perfil),
                contentDescription = "Your Image",
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                modifier = Modifier.size(width = 250.dp, height = 50.dp,),
                onClick = {
                    val intent = Intent(context, Settings::class.java)
                    startActivity(context, intent, null)
                }
            ) {
                Text("Settings", fontSize = 25.sp)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                modifier = Modifier.size(width = 250.dp, height = 50.dp),
                onClick = {
                    val intent = Intent(context, HighScores::class.java)
                    startActivity(context, intent, null)
                }
            ) {
                Text(
                    text = "High Scores",
                    fontSize = 25.sp
                )
            }
            Spacer(modifier = Modifier.height(150.dp))
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
                Text("PLAY NOW", fontSize = 40.sp)
            }
        }
    }
