package com.example.simongame

import android.content.Context
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
import androidx.compose.material3.Text
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
fun MainMenuLayout(context: Context){
    //val userImage = if (thisUser != null)
    //    ImageBitmap.imageResource(R.drawable.ic_launcher_foreground)
    //else
    //    ImageBitmap.imageResource(R.drawable.ic_launcher_foreground)
    //val userName = thisUser?.userName ?: "unknown"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        //Image(
        //    modifier = Modifier.size(100.dp),
        //    contentDescription = "UsersImage",
        //    bitmap = userImage
        //)
        Spacer(modifier = Modifier.height(8.dp))
        //Text(userName)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {

            }
        ) {
            Text("Settings")
        }
        Button(
            onClick = {  }
        ) {
            Text("High Scores")
        }
        Button(
            onClick = {
                //val intent = Intent(context, SettingsActivity::class.java)
                //startActivity(intent)
            }
        ) {
            Text("PLAY NOW")
        }
    }
}