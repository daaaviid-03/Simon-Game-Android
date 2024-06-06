package com.example.simongame.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [GameHistory::class], version = 1)
abstract class GamesHistoryDB : RoomDatabase() {
    abstract fun gameHistoryDAO(): GameHistoryDAO

    companion object {
        private var db: GamesHistoryDB? = null

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