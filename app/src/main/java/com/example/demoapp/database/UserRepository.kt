package com.example.demoapp.database

import com.example.demoapp.models.Users

/**
 * Repository class for handling user info
 */
class UserRepository(private  val usersDAO: UsersDAO) {

    fun getCurrentUserInfo(id:String) : Users = usersDAO.getUserInfo(id)

    fun insertUser(user: Users){
        usersDAO.insertUser(user)
    }
}