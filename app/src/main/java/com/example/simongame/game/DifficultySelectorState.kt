package com.example.simongame.game

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simongame.LEVEL_LEN_INITIAL_SEQUENCE_STEPS
import com.example.simongame.LEVEL_MAX_RESPONSE_TIME_SEC
import com.example.simongame.LEVEL_NAME
import com.example.simongame.LEVEL_VELOCITY_SEC
import com.example.simongame.NUMBER_OF_LEVELS
import com.example.simongame.UpperBarControl

@Composable
fun DifficultySelectorState(context: Context, onclick: (difficulty: Int) -> Unit){

    var difficulty by rememberSaveable { mutableIntStateOf(1) }

    val difficultyRange = 1f..NUMBER_OF_LEVELS.toFloat()
    Column {
        UpperBarControl(context, "Difficulty\n\nSelector")
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally//verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = LEVEL_NAME[difficulty - 1].uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp,
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Slider(
                value = difficulty.toFloat(),
                onValueChange = {
                    difficulty = it.toInt()
                },
                valueRange = difficultyRange,
                steps = NUMBER_OF_LEVELS - 1,

                modifier = Modifier
                    .size(width = 300.dp, height = 150.dp)
                    .weight(1f)
            )
            Spacer(modifier = Modifier.weight(0.5f))
            InfoLineLayout(
                infoText="Initial sequence length:",
                value= LEVEL_LEN_INITIAL_SEQUENCE_STEPS[difficulty - 1].toString(),
                valUnit=""
            )
            InfoLineLayout(
                infoText="Showing velocity:",
                value= LEVEL_VELOCITY_SEC[difficulty - 1].toString(),
                valUnit="s/c"
            )
            InfoLineLayout(
                infoText="Time to respond:",
                value= LEVEL_MAX_RESPONSE_TIME_SEC[difficulty - 1].toString(),
                valUnit="s/c"
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Button(
                onClick = {
                    onclick(difficulty)
                },
                modifier = Modifier
                    .size(width = 300.dp, height = 150.dp)
                    .weight(1f)
            )
            {
                Text("PLAY NOW")
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }
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