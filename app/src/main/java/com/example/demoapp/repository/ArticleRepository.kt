package com.example.demoapp.repository

import androidx.lifecycle.LiveData
import com.example.demoapp.api.APIResponse
import com.example.demoapp.api.Resource
import com.example.demoapp.api.RetrofitManager
import com.example.demoapp.database.ArticlesDAO
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News

/**
 * Repository class for handling news articles
 */
class ArticleRepository(private val articlesDAO: ArticlesDAO) {

    /**
     * Method to read articles from database
     * @return List of [Articles] as a LiveData
     */
    fun readArticles(): LiveData<List<Articles>> = articlesDAO.getAllArticles()

    /**
     * Method to get articles from NewsAPI.org
     * @param page To denote the API page count
     * @param response A callback function to pass [APIResponse] of [News]
     */
    fun getArticles(page:Int, response: (Resource<News>) -> Unit) {
        RetrofitManager.getNewsData(page) {
            when (it) {
                is APIResponse.Success -> {
                    if(page > 1) {
                        response(Resource.loadMore(it.data))
                    } else {
                        response(Resource.success(it.data as News))
                    }
                }
                is APIResponse.Error -> response(Resource.error(it.error))
                is APIResponse.Failure ->response(Resource.failure(it.exception))
            }
        }
    }

    /**
     * Method to add articles to database
     * @param articles An object of class [Articles]
     */
    suspend fun addArticles(articles: Articles) {
        articlesDAO.addArticle(articles)
    }
}