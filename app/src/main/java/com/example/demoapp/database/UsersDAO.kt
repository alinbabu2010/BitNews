package com.example.demoapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.demoapp.models.Users

@Dao
interface UsersDAO {

    @Insert
    fun insertUser(user: Users)

    @Query("SELECT * FROM Users WHERE id=:id")
    fun getUserInfo(id:String) : Users

    @Update
    fun updateUser(user: Users)

    @Query("DELETE FROM Users WHERE id=:id")
    fun deleteUser(id:String)
}