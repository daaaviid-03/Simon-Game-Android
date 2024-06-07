package com.example.simongame.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simongame.NUMBER_OF_LEVELS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.random.Random

class DBViewModel(private var app: Application): AndroidViewModel(app) {

    var last5Names = MutableLiveData<List<String>>(listOf())
    var best10Games = MutableLiveData<MutableList<List<GameHistory>>>(mutableListOf())

    private val db = GamesHistoryDB.getInstance(app.applicationContext)
    private val dao = db.gameHistoryDAO()

    init {
        var best10GamesVal = best10Games.value!!
        for (i in 1..NUMBER_OF_LEVELS) {
            best10GamesVal.add(listOf())
        }
        best10Games.postValue(best10GamesVal)
        println(best10Games.value)
        println(db)
        updateLists()
    }

    private fun updateLists(difficultyLevel: Int? = null) {
        getLast5()
        if (difficultyLevel == null) {
            for (i in 1..NUMBER_OF_LEVELS) {
                getTop10(i)
            }
        } else {
            getTop10(difficultyLevel)
        }

    }

    fun insertGame(userName: String, difficultyLevel: Int, duration: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val thisHist =
                GameHistory(0, userName, Instant.now().toEpochMilli(), difficultyLevel, duration)
            dao.insertGame(thisHist)
            updateLists(difficultyLevel)
        }
    }

    fun getTop10(difficultyLevel: Int) {
        val best10GamesVal = best10Games.value!!
        CoroutineScope(Dispatchers.IO).launch {
            best10GamesVal[difficultyLevel - 1] = dao.getTop10LongestGamesInDifficulty(difficultyLevel)
            best10Games.postValue(best10GamesVal)
        }
    }

    fun getLast5() {
        CoroutineScope(Dispatchers.IO).launch {
            last5Names.postValue(dao.getLast5NamesFromRecords())
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DBViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DBViewModel(application) as T
    }
}





