package com.example.demoapp.database

import androidx.lifecycle.LiveData
import com.example.demoapp.models.Articles

class ArticleRepository(private val  articlesDAO: ArticlesDAO) {

    val readAllData : LiveData<List<Articles>> = articlesDAO.getAllArticles()

    suspend fun addArticles(articles: Articles){
        articlesDAO.addArticle(articles)
    }
}