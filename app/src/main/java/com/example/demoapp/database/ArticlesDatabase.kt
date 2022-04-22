package com.example.demoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.demoapp.models.Articles


@Database(entities = [Articles::class], version = 1)
abstract class ArticlesDatabase : RoomDatabase() {

    abstract fun articlesDAO(): ArticlesDAO

    companion object {

        @Volatile
        private var INSTANCE: ArticlesDatabase? = null

        private val migration2to1: Migration = object : Migration(2, 1) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Articles` (`author` TEXT, `title` TEXT NOT NULL, `description` TEXT, `url` TEXT, `imageUrl` TEXT, `publishedDate` TEXT, `content` TEXT, `source_id` TEXT, `source_name` TEXT, PRIMARY KEY(`title`))")
            }
        }


        fun getDatabase(context: Context): ArticlesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticlesDatabase::class.java, "news_database"
                ).addMigrations(migration2to1).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}