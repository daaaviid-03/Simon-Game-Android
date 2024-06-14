package com.example.simongame.game

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simongame.BOARD_SHAPE_INDEX_KEY
import com.example.simongame.ConfirmExitDialogInformationGame
import com.example.simongame.LEVEL_LEN_INITIAL_SEQUENCE_STEPS
import com.example.simongame.LEVEL_MAX_RESPONSE_TIME_SEC
import com.example.simongame.LEVEL_VELOCITY_SEC
import com.example.simongame.SOUND_LEVEL_KEY
import com.example.simongame.SimonButtonShapeIcons
import com.example.simongame.SimonButtonSound
import com.example.simongame.SimonColorBlue
import com.example.simongame.SimonColorGreen
import com.example.simongame.SimonColorRed
import com.example.simongame.SimonColorYellow
import com.example.simongame.UpperBarControl
import com.example.simongame.confirmExitDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.log

private fun displayEchoAnim(
    text: String,
    echoString: MutableState<String>,
    echoStringAlpha: MutableState<Float>
) {
    CoroutineScope(Dispatchers.Default).launch {
        echoStringAlpha.value = 1f
        echoString.value = text
    }
}

@Composable
fun PlayGameState(
    context: Context,
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    difficulty: Int,
    onclick: (sequenceLen: Int) -> Unit
){
    val hintToPlay = rememberSaveable { mutableStateOf("") }

    val echoString = rememberSaveable { mutableStateOf("") }
    val echoStringAlpha = rememberSaveable { mutableFloatStateOf(0f) }
    val playEchoAnim: (text: String) -> Unit = {
        displayEchoAnim(it, echoString, echoStringAlpha)
    }

    val buttonToDisplay: MutableState<Int?> = rememberSaveable {
        mutableStateOf(null)
    }

    timerVM.timerEnded.observe(context as ComponentActivity) {
        if (it) {
            vm.postNewGameState(GameState.GameOver)
        }
    }

    var gameTimer by rememberSaveable { mutableLongStateOf(0L) }
    timerVM.actualTimeRemaining.observe(context) {
        if (it != gameTimer)
            gameTimer = it
    }

    var gameState by rememberSaveable { mutableStateOf(GameState.NotStarted) }
    var gamStateChanged by rememberSaveable { mutableStateOf(false) }
    vm.gameState.observe(context) {
        if (gameState != it && !gamStateChanged)
            gameState = it
        gamStateChanged = true
        println("GAME STATE = $it")
    }
    if (gamStateChanged) {
        gamStateChanged = false
        when (gameState) {
            GameState.NotStarted -> {
                hintToPlay.value = "NOT STARTED YET WAIT PLEASE!"

                vm.startNewGame(
                    LEVEL_LEN_INITIAL_SEQUENCE_STEPS[difficulty - 1],
                    4
                )
            }

            GameState.Pause -> {
                hintToPlay.value = "DON'T MOVE"

                timerVM.stopTimer()
            }

            GameState.ContinuePlaying -> {
                playEchoAnim("Continue Playing...")

                vm.postNewGameState(GameState.Showing)
            }

            GameState.ListeningStep -> {
                hintToPlay.value = "YOUR TURN!"
            }

            GameState.NextStep -> {
                playEchoAnim("GREAT MOVE!")

                timerVM.stopTimer()
                timerVM.startNewTimer(
                    (LEVEL_MAX_RESPONSE_TIME_SEC[difficulty - 1] * 1000).toLong()
                )
                vm.postNewGameState(GameState.ListeningStep)
            }

            GameState.Showing -> {
                playEchoAnim("It's my turn")
                hintToPlay.value = "WATCH THE MOVES..."

                timerVM.stopTimer()
                timerVM.actualTimeRemaining.postValue((LEVEL_MAX_RESPONSE_TIME_SEC[difficulty - 1] * 1000).toLong())
                showSequence(
                    vm,
                    (LEVEL_VELOCITY_SEC[difficulty - 1] / log(
                        vm.sequence.value!!.size.toDouble() + 10,
                        10.toDouble()
                    )).toFloat(),
                    buttonToDisplay,
                    context
                )
            }

            GameState.GameOver -> {
                playEchoAnim("GAME OVER")
                hintToPlay.value = "GAME OVER"

                timerVM.stopTimer()
                onclick(vm.sequence.value!!.size)
            }
        }
    }
    if (gameState != GameState.Pause) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            ) {
                Button(onClick = {
                    vm.postNewGameState(GameState.Pause)
                    confirmExitDialog(context, ConfirmExitDialogInformationGame) {
                        vm.postNewGameState(GameState.ContinuePlaying)
                    }
                }) {
                    Text(text = "◀")
                }
            }
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            ) {
                Button(onClick = {
                    pauseGame(vm)
                }) {
                    Text(text = "||")
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (gameState == GameState.Pause) {
            PauseScreen(context, vm)
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format(Locale.US, "TIME REMAINING (s) = %.3f", gameTimer / 1000f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = echoString.value,
                    fontSize = 10.sp,
                    modifier = Modifier.alpha(echoStringAlpha.floatValue),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = hintToPlay.value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    SimonButtonLayout(vm, 0, SimonColorRed, buttonToDisplay,context)
                    SimonButtonLayout(vm, 1, SimonColorBlue, buttonToDisplay, context)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    SimonButtonLayout(vm, 2, SimonColorGreen, buttonToDisplay, context)
                    SimonButtonLayout(vm, 3, SimonColorYellow, buttonToDisplay, context)
                }
            }
        }
    }
}

@Composable
private fun PauseScreen(context: Context, vm: GameViewModel) {
    Column{
        UpperBarControl(context, "") {
            confirmExitDialog(context, ConfirmExitDialogInformationGame){
                vm.postNewGameState(GameState.ContinuePlaying)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "GAME\n\n\n\nPAUSED",
                fontSize = 60.sp,
                modifier = Modifier.padding(50.dp),
                color = Color.White
            )
            Button(onClick = {
                pauseGame(vm)
            }) {
                Text(text = "Continue Playing")
            }
        }
    }
}

@Composable
fun SimonButtonLayout(
    vm: GameViewModel,
    buttonId: Int,
    buttonColor: Color,
    buttonToDisplay: MutableState<Int?>,
    context: Context
){
    Button(
        onClick = {
            simonButtonPressed(vm, buttonId, buttonToDisplay, context)
        },
        modifier = Modifier
            .height(200.dp)
            .width(200.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id =
            SimonButtonShapeIcons[
                GameActivity.sharedPreferences.getInt(
                    BOARD_SHAPE_INDEX_KEY, 0)]),
            contentDescription = null,
            colorFilter = ColorFilter.tint(if (buttonToDisplay.value == buttonId) Color.Gray else buttonColor),
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
        )
    }
}

private fun simonButtonPressed(
    vm: GameViewModel,
    buttonId: Int,
    buttonToDisplay: MutableState<Int?>,
    context: Context
){
    if (vm.gameState.value!! == GameState.ListeningStep) {
        animButton(buttonToDisplay, buttonId, context)
        vm.checkLastButtonPressed(buttonId)
    }
}

private fun pauseGame(vm: GameViewModel) {
    if (vm.gameState.value == GameState.Pause) {
        vm.postNewGameState(GameState.ContinuePlaying)
    } else {
        vm.postNewGameState(GameState.Pause)
    }
}
fun playSound(context: Context, soundResId: Int) {
    val currentSoundVolume = GameActivity.sharedPreferences.getInt(SOUND_LEVEL_KEY, 100)
    val value = currentSoundVolume.toFloat()
    val soundFloat = value / 100.0f
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer?.setVolume(soundFloat, soundFloat)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}
private fun animButton(
    buttonToDisplay: MutableState<Int?>,
    buttonId: Int,
    context: Context
) {
    CoroutineScope(Dispatchers.Default).launch {
        buttonToDisplay.value = buttonId
        playSound(context, SimonButtonSound[buttonId])
        delay(200L)
        if (buttonToDisplay.value == buttonId)
            buttonToDisplay.value = null
    }
}

private fun showSequence(vm: GameViewModel, timeToShowStep: Float, buttonToDisplay: MutableState<Int?>, context: Context) {
    println("Showing sequence: ${vm.sequence.value}")
    val timeToWaitBetween =  (timeToShowStep/2 * 1000).toLong()
    CoroutineScope(Dispatchers.Default).launch {
        for (i in vm.sequence.value!!) {
            if (vm.gameState.value == GameState.Showing) {
                delay(timeToWaitBetween)
                animButton(buttonToDisplay, i, context)
                delay(timeToWaitBetween)
            }
        }
        if (vm.gameState.value == GameState.Showing) {
            vm.postNewGameState(GameState.NextStep)
        }
    }
}