package com.example.demoapp.repository

import androidx.lifecycle.LiveData
import com.example.demoapp.database.ArticlesDAO
import com.example.demoapp.models.Articles

/**
 * Repository class for handling news articles
 */
class ArticleRepository(private val  articlesDAO: ArticlesDAO) {

    val readAllData : LiveData<List<Articles>> = articlesDAO.getAllArticles()

    suspend fun addArticles(articles: Articles){
        articlesDAO.addArticle(articles)
    }
}