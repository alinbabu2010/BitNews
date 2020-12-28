package com.example.demoapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.demoapp.models.Users

@Dao
interface UsersDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: Users)

    @Query("SELECT * FROM Users WHERE id=:id")
    fun getUserInfo(id:String) : Users

    @Query("UPDATE Users SET name=:name,username=:username WHERE id=:id ")
    fun updateUser(id:String,name : String,username : String)

    @Query("DELETE FROM Users WHERE id=:id")
    fun deleteUser(id:String)
}