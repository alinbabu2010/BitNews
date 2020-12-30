package com.example.demoapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demoapp.api.APIResponse
import com.example.demoapp.api.Resource
import com.example.demoapp.api.RetrofitManager
import com.example.demoapp.database.ArticlesDAO
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.ui.fragments.dashboard.NewsFragment
import com.example.demoapp.utils.Utils.Companion.checkNetworkConnection

/**
 * Repository class for handling news articles
 */
class ArticleRepository(private val  articlesDAO: ArticlesDAO) {

    val newsLiveData = MutableLiveData<Resource<News?>>()
    val readAllArticles : LiveData<List<Articles>> = articlesDAO.getAllArticles()

    fun getArticles() {
        val articles = readAllArticles
        if (articles.value == null) {
            checkNetworkConnection(NewsFragment.context) {
                RetrofitManager.getRetrofitService {
                    when (it) {
                        is APIResponse.Success -> newsLiveData.postValue(Resource.success(it.data))
                        is APIResponse.Error -> newsLiveData.postValue(Resource.error(it.error))
                        is APIResponse.Failure -> newsLiveData.postValue(Resource.failure(it.exception))
                    }
                }
            }
        }
    }

    suspend fun addArticles(articles: Articles){
        articlesDAO.addArticle(articles)
    }
}