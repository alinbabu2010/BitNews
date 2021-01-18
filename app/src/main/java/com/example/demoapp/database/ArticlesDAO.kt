package com.example.demoapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.demoapp.models.Articles

@Dao
interface ArticlesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticle(articles: List<Articles>)

    @Query("SELECT * FROM `Articles`")
    fun getAllArticles() : List<Articles>

}