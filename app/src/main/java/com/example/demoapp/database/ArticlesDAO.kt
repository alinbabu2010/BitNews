package com.example.demoapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.demoapp.models.Articles

@Dao
interface ArticlesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addArticle(articles: Articles)

    @Query("SELECT * FROM Articles")
    fun getAllArticles() : LiveData<List<Articles>>

}