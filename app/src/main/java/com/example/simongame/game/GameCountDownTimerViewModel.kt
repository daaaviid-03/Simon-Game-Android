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

class GameCountDownTimerViewModel(app: Application): AndroidViewModel(app){

    private var isTimerStopped = MutableLiveData(false)
    var actualTimeRemaining = MutableLiveData(0L)
    var timerEnded = MutableLiveData(false)

    private var timerEndTime = 0L
    private var countDownInterval = 100L
    fun startNewTimer(timerMilliseconds: Long) {
        isTimerStopped.postValue(false)
        timerEnded.postValue(false)
        timerEndTime = Instant.now().toEpochMilli() + timerMilliseconds
        executeTimer()
    }
    fun stopTimer() {
        isTimerStopped.postValue(true)
    }
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
@Suppress("UNCHECKED_CAST")
class GameCountDownTimerViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameCountDownTimerViewModel(application) as T
    }
}
