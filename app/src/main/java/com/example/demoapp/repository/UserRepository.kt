package com.example.demoapp.repository

import com.example.demoapp.database.UsersDAO
import com.example.demoapp.firebase.ProfileFirebase.getDataFromFirebase
import com.example.demoapp.models.Users

/**
 * Repository class for handling user info
 * @param usersDAO An instance of [UsersDAO] interface
 */
class UserRepository(private  val usersDAO: UsersDAO) {

    /**
     * Method to get current user info
     * @param id String value that contains user id
     * @param data Callback function to pass the [Users] object
     */
    fun getCurrentUserInfo(id:String,data : (Users) -> Unit) {
        val user = usersDAO.getUserInfo(id)
        if (user == null) {
            getDataFromFirebase {
                data(it)
            }
        } else data(user)
    }

    /**
     * Method to insert user info to database
     * @param user An object of [Users] containing user data
     */
    fun insertUser(user: Users){
        usersDAO.insertUser(user)
    }

    /**
     * Method to delete user info from database
     * @param uid String value that contains user id
     */
    fun deleteUser(uid:String){
        usersDAO.deleteUser(uid)
    }

    /**
     * Method to update user info in the database
     * @param user An object of [Users] containing modified user data
     */
    fun updateUser(user: Users){
        usersDAO.updateUser(user)
    }
}