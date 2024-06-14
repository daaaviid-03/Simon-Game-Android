package com.example.simongame.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Game countdown timer view model to access to the variables in real time
 */
class GameCountDownTimerViewModel(app: Application): AndroidViewModel(app){

    /**
     * Time remaining in milliseconds
     */
    var actualTimeRemaining = MutableLiveData(0L)

    /**
     * Indicates if the timer has ended
     */
    var timerEnded = MutableLiveData(false)

    /**
     * Indicates if the timer is stopped
     */
    private var isTimerStopped = MutableLiveData(false)

    /**
     * Timer end time in milliseconds
     */
    private var timerEndTime = 0L

    /**
     * Timer interval in milliseconds
     */
    private var countDownInterval = 100L

    /**
     * Starts a new timer with the given time
     */
    fun startNewTimer(timerMilliseconds: Long) {
        isTimerStopped.postValue(false)
        timerEnded.postValue(false)
        timerEndTime = Instant.now().toEpochMilli() + timerMilliseconds
        executeTimer()
    }

    /**
     * Stops the actual timer
     */
    fun stopTimer() {
        isTimerStopped.postValue(true)
    }

    /**
     * Executes the timer in a coroutine
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun executeTimer() {
        CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.ATOMIC) {
            while (!isTimerStopped.value!! && Instant.now().toEpochMilli() < timerEndTime) {
                actualTimeRemaining.postValue(timerEndTime - Instant.now().toEpochMilli())
                delay(countDownInterval)
            }
            if (!isTimerStopped.value!!) {
                timerEnded.postValue(true)
            }
        }
    }
}

/**
 * Factory to create a GameCountDownTimerViewModel
 */
@Suppress("UNCHECKED_CAST")
class GameCountDownTimerViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameCountDownTimerViewModel(application) as T
    }
}
