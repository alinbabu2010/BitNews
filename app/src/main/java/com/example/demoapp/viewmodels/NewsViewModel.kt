package com.example.demoapp.viewmodels

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.adapter.NewsAdapter
import com.example.demoapp.api.RetrofitManager
import com.example.demoapp.api.RetrofitManager.loadNews
import com.example.demoapp.models.News

/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel : ViewModel() {

    var newsLiveData = MutableLiveData<MutableSet<News.Articles>>()
    var newsArticles = ArrayList<News.Articles>()
    private var articles: MutableSet<News.Articles>? = mutableSetOf()

    init {
        newsLiveData.value = mutableSetOf()
    }

    /**
     * Method to get the news from API url
     */
    fun getNews(activity: Activity, recyclerView: RecyclerView) {
        val baseURL = "http://newsapi.org/v2/"
        val call = RetrofitManager.getRetrofitService(baseURL).getNews()
        loadNews(activity,call,baseURL) {
            newsArticles = it.articles.distinctBy { articles ->  articles.title } as ArrayList<News.Articles>
            recyclerView.adapter = NewsAdapter(newsArticles,this)
            recyclerView.setHasFixedSize(true)
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
