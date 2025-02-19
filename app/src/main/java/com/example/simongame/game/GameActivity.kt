package com.example.simongame.game

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.simongame.ui.theme.SimonGameTheme
import android.app.Application
import android.content.SharedPreferences
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simongame.ConfirmExitDialogInformationEndGame
import com.example.simongame.ConfirmExitDialogInformationGame
import com.example.simongame.LEVEL_LEN_INITIAL_SEQUENCE_STEPS
import com.example.simongame.MusicManager
import com.example.simongame.confirmExitDialog
import com.example.simongame.db.DBViewModel
import com.example.simongame.db.DBViewModelFactory
import com.example.simongame.exitThisActivity

/**
 * Main activity of the game
 */
class GameActivity : ComponentActivity() {
    companion object {
        /**
         * State of the game activity
         */
        var state: Int = 0
        /**
         * Shared preferences
         */
        lateinit var sharedPreferences: SharedPreferences
        /**
         * Game view model
         */
        lateinit var vm: GameViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("SimonGame_Preferences", Context.MODE_PRIVATE)

        // Handle the return button pressed
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (state) {
                    1 -> confirmExitDialog(this@GameActivity, ConfirmExitDialogInformationGame)
                    2 -> confirmExitDialog(this@GameActivity, ConfirmExitDialogInformationEndGame)
                    else -> exitThisActivity(this@GameActivity)
                }
            }
        })

        setContent {
            val context = LocalContext.current
            // Initialize game view model
            vm = viewModel(
                factory = GameViewModelFactory(context.applicationContext as Application))
            // Initialize timer view model
            val timerVM: GameCountDownTimerViewModel = viewModel(
                factory = GameCountDownTimerViewModelFactory(context.applicationContext as Application))
            // Initialize database view model
            val dbVM: DBViewModel = viewModel(
                factory = DBViewModelFactory(context.applicationContext as Application))

            SimonGameTheme {
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

    override fun onPause() {
        super.onPause()
        MusicManager.pauseMusic()
        vm.postNewGameState(GameState.Pause)
    }
    override fun onResume() {
        super.onResume()
        MusicManager.resumeMusic()
    }
}

/**
 * Layout of the game
 */
@Composable
fun BackgroundLayout(
    context: Context,
    vm: GameViewModel,
    timerVM: GameCountDownTimerViewModel,
    dbVM: DBViewModel
){
    // Saves the actual difficulty of the game
    var difficulty by rememberSaveable { mutableIntStateOf(0) }
    // Saves the state of the game
    var state by rememberSaveable { mutableIntStateOf(GameActivity.state) }
    // Saves the length of the sequence obtained
    var sequenceLen by rememberSaveable { mutableIntStateOf(0) }
    when (state) {
        0 -> {
            // Displays the difficulty selector
            DifficultySelectorState(context) {
                difficulty = it
                GameActivity.state = 1
                state = 1
            }
        }
        1 -> {
            // Displays the actual game screen
            PlayGameState(context, vm, timerVM, difficulty) {
                sequenceLen = it - LEVEL_LEN_INITIAL_SEQUENCE_STEPS[difficulty - 1]
                GameActivity.state = 2
                state = 2
            }
        }
        else -> {
            // Displays the end game screen
            EndGameState(context, dbVM, sequenceLen, difficulty) {
                dbVM.insertGame(it, difficulty, sequenceLen)
                exitThisActivity(context)
            }
        }
    }
}




