package com.example.demoapp.viewmodels

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.google.gson.GsonBuilder

/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel : ViewModel() {

    var newsLiveData = MutableLiveData<MutableSet<Articles>>()
    var newsArticles = ArrayList<Articles>()
    private var articles: MutableSet<Articles>? = mutableSetOf()

    init {
        newsLiveData.value = mutableSetOf()
    }

    /**
     * Method to get the news from JSON file
     */
    fun getNews(activity: Activity): ArrayList<Articles> {
        val fileData: String? = activity.assets.open("news.json").readBytes().let { String(it) }
        val gson = GsonBuilder().setPrettyPrinting().create()
        gson.fromJson(fileData, News::class.java).let { newsArticles = it.articles }
        return newsArticles
    }

    /**
     * Method to add news article to favourites
     */
    fun addToFavourites(article: Articles?) {
        articles = newsLiveData.value
        article?.let { articles?.add(it) }
        newsLiveData.postValue(articles)
    }

    /**
     * Method to remove news article from favourites
     */
    fun removeFromFavourites(article: Articles?) {
        articles = newsLiveData.value
        articles?.remove(article)
        newsLiveData.postValue(articles)
    }

    /**
     * Method to check news article in favourites set
     */
    fun isFavouriteNews(article: Articles?): Boolean? {
        return newsLiveData.value?.contains(article)
    }

    /**
     * Method to return set of favourites
     */
    fun getFavourites(): MutableSet<Articles>? {
        articles = newsLiveData.value
        return articles
    }

}
