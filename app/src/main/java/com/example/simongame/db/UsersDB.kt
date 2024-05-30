package com.example.simongame2.dbimplementation

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User::class, GameHistory::class], version = 1)
abstract class UsersDB : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun gameDao(): GameHistoryDAO

    companion object {
        private var db: UsersDB? = null

        fun getInstance(context: Context): UsersDB {
            if (db == null) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    UsersDB::class.java,
                    "app_database.db")
                    .createFromAsset("databases/app_database.db")
                    .build()
            }
            return db!!
        }
    }
}