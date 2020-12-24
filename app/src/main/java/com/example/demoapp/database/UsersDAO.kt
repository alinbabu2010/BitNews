package com.example.demoapp.database

import androidx.room.*
import com.example.demoapp.models.Users

@Dao
interface UsersDAO {

    @Insert
    fun insertUser(user: Users)

    @Query("SELECT * FROM Users WHERE id=:id")
    fun getUserInfo(id:String) : Users

    @Update
    fun updateUser(user: Users)

    @Delete
    fun deleteUser(user: Users)
}