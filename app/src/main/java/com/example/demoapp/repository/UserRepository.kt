package com.example.demoapp.repository

import com.example.demoapp.database.UsersDAO
import com.example.demoapp.firebase.ProfileFirebase.Companion.getDataFromFirebase
import com.example.demoapp.models.Users

/**
 * Repository class for handling user info
 */
class UserRepository(private  val usersDAO: UsersDAO) {

    fun getCurrentUserInfo(id:String,data : (Users) -> Unit) {
        val user = usersDAO.getUserInfo(id)
        if (user == null) {
            getDataFromFirebase {
                data(it)
            }
        } else data(user)
    }

    fun insertUser(user: Users){
        usersDAO.insertUser(user)
    }

    fun deleteUser(uid:String){
        usersDAO.deleteUser(uid)
    }

    fun updateUser(user: Users){
        usersDAO.updateUser(user.id, user.name.toString(), user.username.toString())
    }
}