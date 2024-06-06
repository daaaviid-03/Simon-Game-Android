package com.example.simongame.game

import android.annotation.SuppressLint
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
import android.app.Application
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.example.simongame.LEVEL_LEN_INITIAL_SEQUENCE_STEPS
import com.example.simongame.LEVEL_MAX_RESPONSE_TIME_SEC
import com.example.simongame.LEVEL_NAME
import com.example.simongame.LEVEL_VELOCITY_SEC
import com.example.simongame.NUMBER_OF_LEVELS
import com.example.simongame.SimonColorBlue
import com.example.simongame.SimonColorGreen
import com.example.simongame.SimonColorRed
import com.example.simongame.SimonColorYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simongame.db.HistoryRepository
import com.example.simongame.db.GamesHistoryDB
import kotlinx.coroutines.delay

class GameActivity : ComponentActivity() {
    companion object {
        var state: Int = 0
        val simonButtons = mutableListOf<SemanticsNode>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle the return button pressed
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (state == 1){
                    confirmExit(this@GameActivity)
                } else {
                    finish()
                }
            }
        })

        setContent {
            val context = LocalContext.current

            val vm: GameViewModel = viewModel(
                factory = GameViewModelFactory(context.applicationContext as Application)
            )

            val timerVM: GameCountDownTimerViewModel = viewModel(
                factory = GameCountDownTimerViewModelFactory(context.applicationContext as Application)
            )

            val db = GamesHistoryDB.getInstance(context)
            val histRep = HistoryRepository(db.gameHistoryDAO())

            SimonGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color =  MaterialTheme.colorScheme.background
                ) {
                    state = 0
                    BackgroundLayout(context, vm, timerVM, histRep)
                }
            }
        }
    }
}

@Composable
fun BackgroundLayout(
    context: Context,
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    histRep: HistoryRepository
){
    var difficulty by rememberSaveable { mutableIntStateOf(0) }
    var state by rememberSaveable { mutableIntStateOf(GameActivity.state) }
    var sequenceLen by rememberSaveable { mutableIntStateOf(0) }
    when (state) {
        0 -> {
            DifficultySelectorActivity(context) {
                difficulty = it
                GameActivity.state = 1
                state = 1
            }
        }
        1 -> {
            PlayGameActivity(context, vm, timerVM, difficulty) {
                sequenceLen = it
                GameActivity.state = 2
                state = 2
            }
        }
        else -> {
            EndGameActivity(histRep, sequenceLen, difficulty) {
                histRep.insertGame(it, difficulty, sequenceLen)
                (context as Activity).finish()
            }
        }
    }
}

@Composable
fun EndGameActivity(
    histRep: HistoryRepository,
    sequenceLen: Int,
    difficulty: Int,
    onclick: (name: String) -> Unit
) {

    val recentRecordNames by rememberSaveable { mutableStateOf(histRep.getLast5()) }

    //val recentRecordNames = mutableListOf("David", "Carlos", "Bruno", "Lucia", "Carolina")

    val userName: MutableState<String> = rememberSaveable { mutableStateOf("") }

    val withError = rememberSaveable { mutableStateOf(false) }

    val tryToConfirm: (name: String) -> Unit = {
        if (it.isNotEmpty()) {
            onclick(it)
        } else {
            withError.value = true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "You have reached $sequenceLen steps in level $difficulty.", fontSize = 20.sp)
        Text(text = "Choose your nickname to save the record:", fontSize = 20.sp)
        FilterMe(userName, tryToConfirm)
        if (withError.value) {
            Text(text = "You can't leave the field empty. Please enter a nickname", fontSize = 20.sp, color = Color.Red)
        }
        Text(text = "Recent usernames:", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(recentRecordNames) {
                Text(
                    text = it,
                    fontSize = 40.sp,
                    modifier = Modifier.clickable {
                        userName.value = it
                    }
                )
            }
        }
    }
}

@Composable
fun FilterMe(userName: MutableState<String>, onclick: (userNameStr: String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = userName.value,
        onValueChange = {
            userName.value = it
        },
        placeholder = {
            Text(text = "Filter")
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
            onclick(userName.value)
        }),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF0F0F0)
        ),
        trailingIcon = {
            Icon(
                Icons.Rounded.Done,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp, 24.dp)
                    .clickable {
                        keyboardController?.hide()
                        onclick(userName.value)
                    })
        }
    )
}

@Composable
fun DifficultySelectorActivity(context: Context, onclick: (difficulty: Int) -> Unit){

    var difficulty by rememberSaveable { mutableIntStateOf(1) }

    val difficultyRange = 1f..NUMBER_OF_LEVELS.toFloat()
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
            value=LEVEL_LEN_INITIAL_SEQUENCE_STEPS[difficulty - 1].toString(),
            valUnit=""
        )
        InfoLineLayout(
            infoText="Showing velocity:",
            value=LEVEL_VELOCITY_SEC[difficulty - 1].toString(),
            valUnit="s/c"
        )
        InfoLineLayout(
            infoText="Time to respond:",
            value=LEVEL_MAX_RESPONSE_TIME_SEC[difficulty - 1].toString(),
            valUnit="s/c"
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Button(
            onClick = {
                onclick(difficulty)
            },
            modifier = Modifier
                .size(width = 300.dp, height = 150.dp)
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

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PlayGameActivity(
    context: Context,
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    difficulty: Int,
    onclick: (sequenceLen: Int) -> Unit
){
    var gameState by rememberSaveable { mutableStateOf("") }

    vm.gameState.observe(context as ComponentActivity) {
        when (it) {
            GameState.NotStarted -> {
                gameState = "Not Started"
            }
            GameState.Pause -> {
                gameState = "Paused"
            }
            GameState.ContinuePlaying -> {
                gameState = "Continue Playing"
            }
            GameState.ListeningStep -> {
                gameState = "Listening Step"
            }
            GameState.NextStep -> {
                gameState = "Next Step"
            }
            GameState.Showing -> {
                gameState = "Showing: ${vm.sequence.value.toString()}"
            }
            GameState.GameOver -> {
                gameState = "Game Over"
            }
            else -> {}
        }
    }

    var gameTimer by rememberSaveable { mutableLongStateOf(0L) }
    timerVM.actualTimeRemaining.observe(context as ComponentActivity) {
        gameTimer = it
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
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

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Timer (ms) = $gameTimer")
        Text(text = "Game State = $gameState")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            SimonButtonLayout(vm, 0, SimonColorRed, "RED")
            SimonButtonLayout(vm, 1, SimonColorBlue, "BLUE")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            SimonButtonLayout(vm, 2, SimonColorGreen, "GREEN")
            SimonButtonLayout(vm, 3, SimonColorYellow, "YELLOW")
        }
    }
    observeViewModels(vm, timerVM, context, difficulty, onclick)
}

private fun pauseGame(vm: GameViewModel) {
    if (vm.gameState.value == GameState.Pause) {
        vm.postNewGameState(GameState.ContinuePlaying)
    } else {
        vm.postNewGameState(GameState.Pause)
    }
}

fun observeViewModels(
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    context: Context,
    difficulty: Int,
    onclick: (sequenceLen: Int) -> Unit
) {
    vm.gameState.observe(context as ComponentActivity) {
        when (it!!) {
            GameState.NotStarted -> {
                vm.startNewGame(
                    LEVEL_LEN_INITIAL_SEQUENCE_STEPS[difficulty - 1],
                    4)
            }
            GameState.Pause -> {
                timerVM.stopTimer()
            }
            GameState.ContinuePlaying -> {
                //timerVM.restartTimer()
                //vm.postNewGameState(GameState.ListeningStep)
                vm.postNewGameState(GameState.NextStep)
            }
            GameState.ListeningStep -> {

            }
            GameState.NextStep -> {
                timerVM.stopTimer()
                timerVM.startNewTimer(
                    (LEVEL_MAX_RESPONSE_TIME_SEC[difficulty - 1] * 1000).toLong()
                )
                vm.postNewGameState(GameState.ListeningStep)
            }
            GameState.Showing -> {
                timerVM.stopTimer()
                showSequence(vm, context, LEVEL_VELOCITY_SEC[difficulty - 1])
            }
            GameState.GameOver -> {
                timerVM.stopTimer()
                onclick(vm.sequence.value!!.size)
            }
        }
    }

    timerVM.timerEnded.observe(context as ComponentActivity) {
        if (it) {
            vm.postNewGameState(GameState.GameOver)
        }
    }
}

fun showSequence(vm: GameViewModel, context: Context, timeToShowStep: Float) {
    CoroutineScope(Dispatchers.IO).launch {
        val timeToWaitBetween = (timeToShowStep/2 * 1000).toLong()
        for (i in vm.sequence.value!!.iterator()) {
            delay(timeToWaitBetween)
//            vm.getInteractionButtonFromList(i).emit(
//                PressInteraction.Press(pressPosition = Offset.Zero))
            delay(timeToWaitBetween)
        }
        vm.postNewGameState(GameState.NextStep)
    }
}

@Composable
fun SimonButtonLayout(vm: GameViewModel, buttonId: Int, buttonColor: Color, buttonText: String){
//    var buttonTrigger by rememberSaveable { mutableStateOf(false) }
//    vm.buttonTriggers.observe(LocalContext.current as ComponentActivity) {
//        buttonTrigger = it[buttonId]
//    }
//    val buttonDisabled by rememberSaveable { mutableStateOf(vm.buttonsDisabled) }
//
//    val interSource = remember { MutableInteractionSource() }
    Button(
        onClick = {
            simonButtonPressed(vm, buttonId)
//            if (!buttonDisabled.value!!) {
//                buttonTrigger = true
//
//            }
                  },
        modifier = Modifier
            .height(200.dp)
            .width(200.dp),
        colors = ButtonDefaults.buttonColors(buttonColor) // if (buttonTrigger) Color.Gray else
        //interactionSource = interSource
    ) {
        Text(text = buttonText)
    }

//    AnimatedContent(
//        targetState = buttonTrigger,
//    ) {
//        Button(
//            onClick = { simonButtonPressed(vm, buttonId) },
//            colors = ButtonDefaults.buttonColors(if (it) Color.Gray else buttonColor)
//        ) {
//            Text(buttonText)
//        }
//    }


    //vm.addInteractionButtonToList(interSource) // --------------------------------------------------------------
}

fun simonButtonPressed(vm: GameViewModel, buttonId: Int){
    if (vm.gameState.value!! == GameState.ListeningStep) {
        vm.checkLastButtonPressed(buttonId)
    }
}



