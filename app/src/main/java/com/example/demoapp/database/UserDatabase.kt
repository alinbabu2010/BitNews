package com.example.demoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.demoapp.models.Users

@Database(entities = [Users::class],version = 2)
abstract class UserDatabase : RoomDatabase() {

    abstract fun usersDAO() : UsersDAO

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    UserDatabase::class.java,"news_database").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }

}