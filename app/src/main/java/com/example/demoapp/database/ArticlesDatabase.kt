package com.example.demoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.demoapp.models.Articles


@Database(entities = [Articles::class], version = 1, exportSchema = false)
abstract class ArticlesDatabase : RoomDatabase() {

    abstract fun articlesDAO() : ArticlesDAO

    companion object {

        @Volatile
        private var INSTANCE: ArticlesDatabase? = null

        fun getDatabase(context: Context): ArticlesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticlesDatabase::class.java, "news_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}