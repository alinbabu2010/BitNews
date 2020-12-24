package com.example.demoapp.database

import androidx.lifecycle.LiveData
import com.example.demoapp.models.Users

/**
 * Repository class for handling user info
 */
class UserRepository(private  val usersDAO: UsersDAO) {

    fun getCurrentUserInfo(uid:String) : LiveData<Users> = usersDAO.getUserInfo(uid)

    fun insertUser(user: Users){
        usersDAO.insertUser(user)
    }
}