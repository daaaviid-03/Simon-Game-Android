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
import android.app.Application
import android.content.SharedPreferences
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import com.example.simongame.BOARD_SHAPE_INDEX_KEY
import com.example.simongame.ConfirmExitDialogInformationEndGame
import com.example.simongame.ConfirmExitDialogInformationGame
import com.example.simongame.SimonButtonShapeIcons
import com.example.simongame.confirmExitDialog
import com.example.simongame.db.DBViewModel
import com.example.simongame.db.DBViewModelFactory
import kotlinx.coroutines.delay

class GameActivity : ComponentActivity() {

    companion object {
        var state: Int = 0
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("SimonGame_Preferences", Context.MODE_PRIVATE)

        // Handle the return button pressed
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (state == 1){
                    confirmExitDialog(this@GameActivity, ConfirmExitDialogInformationGame)
                } else if (state == 2) {
                    confirmExitDialog(this@GameActivity, ConfirmExitDialogInformationEndGame)
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

            val dbVM: DBViewModel = viewModel(
                factory = DBViewModelFactory(context.applicationContext as Application)
            )

            SimonGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color =  MaterialTheme.colorScheme.background
                ) {
                    state = 0
                    BackgroundLayout(context, vm, timerVM, dbVM)
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
    dbVM: DBViewModel
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
            EndGameActivity(context, dbVM, sequenceLen, difficulty) {
                dbVM.insertGame(it, difficulty, sequenceLen)
                (context as Activity).finish()
            }
        }
    }
}

@Composable
fun EndGameActivity(
    context: Context,
    dbVM: DBViewModel,
    sequenceLen: Int,
    difficulty: Int,
    onclick: (name: String) -> Unit
) {

    var recentRecordNames by rememberSaveable {
        mutableStateOf(
            listOf<String>()
        )
    }

    dbVM.last5Names.observe(LocalContext.current as ComponentActivity) {
        recentRecordNames = it!!
    }

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
        NickNameEntry(userName, tryToConfirm)
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
        Spacer(modifier = Modifier.height(100.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(20.dp)
        ){
            Button(
                onClick = {
                    confirmExitDialog(context, ConfirmExitDialogInformationEndGame)
                },
                modifier = Modifier.padding(20.dp)
            ) {
                Text("Don't Save", fontSize = 40.sp)
            }
            Button(
                onClick = {
                    tryToConfirm(userName.value)
                },
                modifier = Modifier.padding(20.dp)
            ) {
                Text("Save", fontSize = 40.sp)
            }
        }

    }
}

@Composable
fun NickNameEntry(userName: MutableState<String>, onclick: (userNameStr: String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = userName.value,
        onValueChange = {
            userName.value = it
        },
        placeholder = {
            Text(text = "Nickname")
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

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PlayGameActivity(
    context: Context,
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    difficulty: Int,
    onclick: (sequenceLen: Int) -> Unit
){
    var gameStateStr by rememberSaveable { mutableStateOf("") }
    var gameState by rememberSaveable { mutableStateOf(GameState.NotStarted) }

    val gameButtonsStates: MutableState<MutableList<Boolean>> = rememberSaveable {
        mutableStateOf(
            MutableList(4){false}
        )
    }

//    vm.gameButtonStates.observe(context as ComponentActivity) {
//        gameButtonsStates.value = it
//    }

    vm.gameState.observe(context as ComponentActivity) {
        gameState = it
        when (it) {
            GameState.NotStarted -> {
                gameStateStr = "Not Started"
            }
            GameState.Pause -> {
                gameStateStr = "Paused"
            }
            GameState.ContinuePlaying -> {
                gameStateStr = "Continue Playing"
            }
            GameState.ListeningStep -> {
                gameStateStr = "Listening Step"
            }
            GameState.NextStep -> {
                gameStateStr = "Next Step"
            }
            GameState.Showing -> {
                gameStateStr = "Showing: ${vm.sequence.value.toString()}"
            }
            GameState.GameOver -> {
                gameStateStr = "Game Over"
            }
            else -> {}
        }
    }

    var gameTimer by rememberSaveable { mutableLongStateOf(0L) }
    timerVM.actualTimeRemaining.observe(context) {
        gameTimer = it
    }
    if (gameState == GameState.Pause) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .alpha(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GAME\n\nPAUSED", fontSize = 60.sp, modifier = Modifier.padding(50.dp), color = Color.White)
            Button(onClick = {
                pauseGame(vm)
            }) {
                Text(text = "Continue Playing")
            }
        }
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
                vm.postNewGameState(GameState.Pause)
                confirmExitDialog(context, ConfirmExitDialogInformationGame){
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

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Timer (ms) = $gameTimer")
        Text(text = "Game State = $gameStateStr")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            SimonButtonLayout(vm, 0, SimonColorRed, "RED", gameButtonsStates)
            SimonButtonLayout(vm, 1, SimonColorBlue, "BLUE", gameButtonsStates)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            SimonButtonLayout(vm, 2, SimonColorGreen, "GREEN", gameButtonsStates)
            SimonButtonLayout(vm, 3, SimonColorYellow, "YELLOW", gameButtonsStates)
        }
    }
    ObserveViewModels(vm, timerVM, context, difficulty, gameButtonsStates, onclick)
}

private fun pauseGame(vm: GameViewModel) {
    if (vm.gameState.value == GameState.Pause) {
        vm.postNewGameState(GameState.ContinuePlaying)
    } else {
        vm.postNewGameState(GameState.Pause)
    }
}
fun ObserveViewModels(
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    context: Context,
    difficulty: Int,
    gameButtonsStates: MutableState<MutableList<Boolean>>,
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
                vm.postNewGameState(GameState.Showing)
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
                timerVM.actualTimeRemaining.postValue((LEVEL_MAX_RESPONSE_TIME_SEC[difficulty - 1] * 1000).toLong())
                showSequence(vm, context, LEVEL_VELOCITY_SEC[difficulty - 1], gameButtonsStates)
            }
            GameState.GameOver -> {
                timerVM.stopTimer()
                onclick(vm.sequence.value!!.size)
            }
        }
    }

    timerVM.timerEnded.observe(context) {
        if (it) {
            vm.postNewGameState(GameState.GameOver)
        }
    }
}
fun showSequence(vm: GameViewModel, context: Context, timeToShowStep: Float, gameButtonsStates: MutableState<MutableList<Boolean>>) {
    CoroutineScope(Dispatchers.IO).launch {
        val timeToWaitBetween = (timeToShowStep/2 * 1000).toLong()
        for (i in vm.sequence.value!!.iterator()) {
            if(vm.gameState.value == GameState.Showing) {
                delay(timeToWaitBetween)
                gameButtonsStates.value[i] = true
                delay(200L)
                gameButtonsStates.value[i] = false
                //animButton(gameButtonsStates, i)
                delay(timeToWaitBetween)
            }
        }
        if(vm.gameState.value == GameState.Showing) {
            vm.postNewGameState(GameState.NextStep)
        }
    }
}

@Composable
fun SimonButtonLayout(
    vm: GameViewModel,
    buttonId: Int,
    buttonColor: Color,
    buttonText: String,
    gameButtonsStates: MutableState<MutableList<Boolean>>
){
    Button(
        onClick = {
            simonButtonPressed(vm, buttonId, gameButtonsStates)
                  },
        modifier = Modifier
            .height(200.dp)
            .width(200.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = SimonButtonShapeIcons[GameActivity.sharedPreferences.getInt(BOARD_SHAPE_INDEX_KEY, 0)]), //
            contentDescription = buttonText,
            colorFilter = ColorFilter.tint(if (!gameButtonsStates.value[buttonId]) buttonColor else Color.Gray),
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
        )
    }
}



fun simonButtonPressed(
    vm: GameViewModel,
    buttonId: Int,
    gameButtonsStates: MutableState<MutableList<Boolean>>
){
    if (vm.gameState.value!! == GameState.ListeningStep) {
        animButton(gameButtonsStates, buttonId)
        vm.checkLastButtonPressed(buttonId)
    }
}

private fun animButton(
    gameButtonsStates: MutableState<MutableList<Boolean>>,
    buttonId: Int
) {
    CoroutineScope(Dispatchers.IO).launch {
        gameButtonsStates.value[buttonId] = true
        delay(200L)
        gameButtonsStates.value[buttonId] = false
    }
}



