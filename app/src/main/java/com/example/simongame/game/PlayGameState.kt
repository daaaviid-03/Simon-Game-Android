package com.example.simongame.game

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
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
import com.example.simongame.confirmExitDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.log

/**
 * Display echo animation for extra information
 */
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

/**
 * Play game layout
 */
@Composable
fun PlayGameState(
    context: Context,
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    difficulty: Int,
    onclick: (sequenceLen: Int) -> Unit
){
    // Hint to play to show
    val hintToPlay = rememberSaveable { mutableStateOf("") }
    // Echo text to show
    val echoString = rememberSaveable { mutableStateOf("") }
    // Echo text alpha value
    val echoStringAlpha = rememberSaveable { mutableFloatStateOf(0f) }
    /**
     * Echo animation execute
     */
    val playEchoAnim: (text: String) -> Unit = {
        displayEchoAnim(it, echoString, echoStringAlpha)
    }
    // Button to anim
    val buttonToDisplay: MutableState<Int?> = rememberSaveable {
        mutableStateOf(null)
    }
    // Observe timer in case it ended
    timerVM.timerEnded.observe(context as ComponentActivity) {
        if (it) {
            vm.postNewGameState(GameState.GameOver)
        }
    }
    // Actual time remaining value
    var gameTimer by rememberSaveable { mutableLongStateOf(0L) }
    // Observe timer to change game timer
    timerVM.actualTimeRemaining.observe(context) {
        if (it != gameTimer)
            gameTimer = it
    }
    // Actual game state
    var gameState by rememberSaveable { mutableStateOf(GameState.NotStarted) }
    // Check if the game state has changed
    var gamStateChanged by rememberSaveable { mutableStateOf(false) }
    // Observe game state
    vm.gameState.observe(context) {
        if (gameState != it && !gamStateChanged)
            gameState = it
        gamStateChanged = true
    }
    // In case the game state has changed
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
    // Display the upper menu layout if the game is not in pause
    if (gameState != GameState.Pause) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            ) {
                Button(onClick = {
                    vm.postNewGameState(GameState.Pause)
                    confirmExitDialog(context, ConfirmExitDialogInformationGame,
                        extraFunctionToCancel = {
                            vm.postNewGameState(GameState.ContinuePlaying)
                        }
                    )
                }) {
                    Text(text = "◀")
                }
            }
            // Pause button
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
    // Game layout
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (gameState == GameState.Pause) {
            // Pause screen
            PauseScreen(context, vm)
        } else {
            // Game screen
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display the actual step level
                Text(
                    text = "STEPS COMPLETED = ${vm.sequence.value!!.size - LEVEL_LEN_INITIAL_SEQUENCE_STEPS[difficulty - 1]}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                // Display time remaining
                Text(
                    text = String.format(Locale.US, "TIME REMAINING (s) = %.3f", gameTimer / 1000f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                // Display echo text
                Text(
                    text = echoString.value,
                    fontSize = 10.sp,
                    modifier = Modifier.alpha(echoStringAlpha.floatValue),
                    color = MaterialTheme.colorScheme.primary
                )
                // Display the hint text
                Text(text = hintToPlay.value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                // Display the simon buttons layout
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

/**
 * Pause screen layout
 */
@Composable
private fun PauseScreen(context: Context, vm: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pause text
        Text(
            "GAME\n\n\n\nPAUSED",
            fontSize = 60.sp,
            modifier = Modifier.padding(50.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        // Exit button
        Button(modifier = Modifier.padding(20.dp),
            onClick = {
                confirmExitDialog(context, ConfirmExitDialogInformationGame,
                    extraFunctionToCancel = {
                        vm.postNewGameState(GameState.ContinuePlaying)
                    }
                )
            }) {
            Text(text = "EXIT")
        }
        // Resume button
        Button(modifier = Modifier.padding(20.dp),
            onClick = {
                pauseGame(vm)
            }) {
            Text(text = "RESUME")
        }
    }
}

/**
 * Simon button layout
 */
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
        // Display the simon button image
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

/**
 * Action when a simon button is pressed
 */
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

/**
 * Pause game function
 */
private fun pauseGame(vm: GameViewModel) {
    if (vm.gameState.value == GameState.Pause) {
        vm.postNewGameState(GameState.ContinuePlaying)
    } else {
        vm.postNewGameState(GameState.Pause)
    }
}

/**
 * Play sound depending on the button ID function
 */
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

/**
 * Anim button function
 */
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

/**
 * Show sequence in background coroutine
 */
private fun showSequence(vm: GameViewModel, timeToShowStep: Float, buttonToDisplay: MutableState<Int?>, context: Context) {
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