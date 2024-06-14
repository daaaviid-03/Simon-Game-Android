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

/**
 * ViewModel for the database
 */
class DBViewModel(app: Application): AndroidViewModel(app) {
    /**
     * Holds the last 5 names from the records
     */
    var last5Names = MutableLiveData<List<String>>(listOf())
    /**
     * Holds the 10 longest games in each difficulty level
     */
    var best10Games = MutableLiveData<MutableList<List<GameHistory>>>(mutableListOf())

    /**
     * DB instance
     */
    private val db = GamesHistoryDB.getInstance(app.applicationContext)

    /**
     * DAO instance to interact with
     */
    private val dao = db.gameHistoryDAO()

    init {
        // Initialize the lists
        val best10GamesVal = best10Games.value!!
        for (i in 1..NUMBER_OF_LEVELS) {
            best10GamesVal.add(listOf())
        }
        best10Games.postValue(best10GamesVal)
        updateLists()
    }

    /**
     * Update the lists in a difficulty level
     */
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

    /**
     * Insert a game record in the database
     */
    fun insertGame(userName: String, difficultyLevel: Int, duration: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val thisHist =
                GameHistory(0, userName, Instant.now().toEpochMilli(), difficultyLevel, duration)
            dao.insertGame(thisHist)
            updateLists(difficultyLevel)
        }
    }

    /**
     * Get the 10 longest games in a difficulty level
     */
    private fun getTop10(difficultyLevel: Int) {
        val best10GamesVal = best10Games.value!!
        CoroutineScope(Dispatchers.IO).launch {
            best10GamesVal[difficultyLevel - 1] = dao.getTop10LongestGamesInDifficulty(difficultyLevel)
            best10Games.postValue(best10GamesVal)
        }
    }

    /**
     * Get the last 5 names from the records
     */
    private fun getLast5() {
        CoroutineScope(Dispatchers.IO).launch {
            last5Names.postValue(dao.getLast5NamesFromRecords())
        }
    }
}

/**
 * Factory for DBViewModel
 */
@Suppress("UNCHECKED_CAST")
class DBViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DBViewModel(application) as T
    }
}





