package com.example.demoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.demoapp.models.Articles


@Database(entities = [Articles::class], version = 2)
abstract class ArticlesDatabase : RoomDatabase() {

    abstract fun articlesDAO(): ArticlesDAO

    companion object {

        private val migration1to2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE `Articles`")
                database.execSQL("CREATE TABLE `Articles` (`author` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `url` TEXT, `imageUrl` TEXT, `publishedDate` TEXT, `content` TEXT, `source_id` TEXT, `source_name` TEXT, PRIMARY KEY(`title`, `author`))")
            }

        }

        @Volatile
        private var INSTANCE: ArticlesDatabase? = null

        fun getDatabase(context: Context): ArticlesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticlesDatabase::class.java, "news_database"
                ).addMigrations(migration1to2).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}