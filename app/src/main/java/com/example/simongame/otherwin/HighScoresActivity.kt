package com.example.simongame.otherwin

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simongame.ui.theme.SimonGameTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simongame.LEVEL_NAME
import com.example.simongame.MainActivity
import com.example.simongame.NUMBER_OF_LEVELS
import com.example.simongame.UpperBarControl
import com.example.simongame.db.DBViewModel
import com.example.simongame.db.DBViewModelFactory
import com.example.simongame.db.GameHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HighScoresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val dbVM: DBViewModel = viewModel(
                factory = DBViewModelFactory(context.applicationContext as Application))

            SimonGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ResultsScreen(context, dbVM)
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
fun ResultsScreen(context: Context, dbVM: DBViewModel) {

    var thisDifficulty by rememberSaveable { mutableIntStateOf(1) }

    var top10Records by rememberSaveable { mutableStateOf(listOf<GameHistory>()) }

    val onDiffChanged: () -> Unit = {
        top10Records = dbVM.best10Games.value!![thisDifficulty - 1]
    }

    dbVM.best10Games.observe(context as ComponentActivity) {
        onDiffChanged()
    }

    val difficultyRange = 1f..NUMBER_OF_LEVELS.toFloat()

    onDiffChanged()

    Column {
        UpperBarControl(context, "High Scores")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Difficulty: ${LEVEL_NAME[thisDifficulty - 1].uppercase()}",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Slider(
                value = thisDifficulty.toFloat(),
                onValueChange = {
                    thisDifficulty = it.toInt()
                    onDiffChanged()
                },
                valueRange = difficultyRange,
                steps = NUMBER_OF_LEVELS - 1,

                modifier = Modifier
                    .size(width = 300.dp, height = 30.dp)
                    .height(50.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(top10Records.size) {
                    RecordCard(it, top10Records[it])
                }
            }
        }
    }
}

@Composable
fun RecordCard(position: Int, gameHistory: GameHistory) {
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ){
            Text(
                text = "${position + 1}. ",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Column {
                Text(text = "Nickname: ${gameHistory.userName}", fontWeight = FontWeight.Bold)
                Text(text = "Difficulty: ${gameHistory.difficultyLevel}")
                Text(text = "Date: ${formatDate(gameHistory.date)}")
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "${gameHistory.duration}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            }
        }
    }
}

fun formatDate(epochMilliseconds: Long): String {
    val date = Date(epochMilliseconds)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return formatter.format(date)
}

