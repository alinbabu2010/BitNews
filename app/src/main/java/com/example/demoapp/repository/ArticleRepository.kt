package com.example.demoapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val newsLiveData = MutableLiveData<Resource<News?>>()

    /**
     * Method to read articles from database
     * @return List of [Articles] as a LiveData
     */
    fun readArticles(): LiveData<List<Articles>> = articlesDAO.getAllArticles()

    /**
     * Method to get articles from NewsAPI.org
     * @param page To denote the API page count
     */
    fun getArticles(page:Int) {
        RetrofitManager.getNewsData(page) {
            when (it) {
                is APIResponse.Success -> {
                    if(page > 1) {
                        newsLiveData.postValue(Resource.loadMore(it.data))
                    } else {
                        newsLiveData.postValue(Resource.success(it.data))
                    }
                }
                is APIResponse.Error -> newsLiveData.postValue(Resource.error(it.error))
                is APIResponse.Failure -> newsLiveData.postValue(Resource.failure(it.exception))
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