package com.example.simongame.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Data Base Singleton for storing games history
 */
@Database(entities = [GameHistory::class], version = 1, exportSchema = false)
abstract class GamesHistoryDB : RoomDatabase() {
    /**
     * DAO for GameHistory
     */
    abstract fun gameHistoryDAO(): GameHistoryDAO

    companion object {
        /**
         * Data Base instance
         */
        private var db: GamesHistoryDB? = null

        /**
         * Get data base instance
         */
        fun getInstance(context: Context): GamesHistoryDB {
            if (db == null) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    GamesHistoryDB::class.java,
                    "games_history.db"
                )
                    .createFromAsset("games_history.db")
                    .build()
            }
            return db!!
        }
    }
}