package com.example.simongame2.dbimplementation

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class UserRepository(private val dao: UserDAO) {

    fun insertUser(name: String){
        val thisUser = User(0, "", name, 100)
        dao.insertUser(thisUser)
    }

    fun updateUser(userId: Int, image: String?, userName: String?, volumeLevel: Int?){
        val thisUser = dao.getUser(userId)
        thisUser?.let {
            image?.let { thisUser.image = image }
            userName?.let { thisUser.userName = userName }
            volumeLevel?.let { thisUser.volumeLevel = volumeLevel }
            dao.updateUser(thisUser)
        }
    }

    fun getUser(userId: Int): User? {
        return dao.getUser(userId)
    }
}