package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.api.Resource
import com.example.demoapp.api.RetrofitManager
import com.example.demoapp.api.RetrofitManager.loadData
import com.example.demoapp.models.News


/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel : ViewModel() {

    var newsLiveData = MutableLiveData<MutableSet<News.Articles>>()
    var newsArticles = MutableLiveData<Resource<News?>>()
    private var articles: MutableSet<News.Articles>? = mutableSetOf()

    init {
        newsLiveData.value = mutableSetOf()
    }

    /**
     * Method to get the news from API url
     */
    fun getNews() {
        val baseURL = "http://newsapi.org/v2/"
        val call = RetrofitManager.getRetrofitService(baseURL).getNews()
        loadData(call, baseURL){
            newsArticles.value = it
        }

    }

    /**
     * Method to add news article to favourites
     */
    fun addToFavourites(article: News.Articles?) {
        articles = newsLiveData.value
        article?.let { articles?.add(it) }
        newsLiveData.postValue(articles)
    }

    /**
     * Method to remove news article from favourites
     */
    fun removeFromFavourites(article: News.Articles?) {
        articles = newsLiveData.value
        articles?.remove(article)
        newsLiveData.postValue(articles)
    }

    /**
     * Method to check news article in favourites set
     */
    fun isFavouriteNews(article: News.Articles?): Boolean? {
        return newsLiveData.value?.contains(article)
    }

    /**
     * Method to return set of favourites
     */
    fun getFavourites(): MutableSet<News.Articles>? {
        articles = newsLiveData.value
        return articles
    }

}
