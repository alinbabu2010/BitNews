package com.example.demoapp.viewmodels

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.adapter.NewsAdapter
import com.example.demoapp.api.NewsRepository
import com.example.demoapp.models.Articles

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
     * Method to get the news from API url
     */
    fun getNews(activity: Activity, recyclerView: RecyclerView) {
        val newsRepository = NewsRepository()
        newsRepository.loadNews(activity) {
            newsArticles = it.distinctBy { articles ->  articles.title } as ArrayList<Articles>
            recyclerView.adapter = NewsAdapter(newsArticles,this)
            recyclerView.setHasFixedSize(true)
        }
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
