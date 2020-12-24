package com.example.demoapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.demoapp.models.Users

@Dao
interface UsersDAO {

    @Insert
    fun insertUser(user: Users)

    @Query("SELECT * FROM Users WHERE id=:uid")
    fun getUserInfo(uid:String) : LiveData<Users>

    @Update
    fun updateUser(user: Users)

    @Delete
    fun deleteUser(user: Users)
}