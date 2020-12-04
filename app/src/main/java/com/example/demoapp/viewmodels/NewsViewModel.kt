package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.api.Resource
import com.example.demoapp.api.RetrofitManager
import com.example.demoapp.models.News


/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel : ViewModel() {

    val newsLiveData = MutableLiveData<Resource<News?>>()

    val newsArticles : ArrayList<News.Articles> by lazy {
            ArrayList()
    }

    val favouritesLiveData = MutableLiveData<MutableSet<News.Articles>>()
    private var favouriteArticles : MutableSet<News.Articles>? = mutableSetOf()

    init {
        favouritesLiveData.value = mutableSetOf()
    }

    /**
     * Method to get the news from API url
     */
    fun getNews() {
        RetrofitManager.getRetrofitService {
            newsLiveData.postValue(it)
            it.data?.articles?.let { it1 -> newsArticles.addAll(it1) }
        }
    }

    /**
     * Method to add news article to favourites
     */
    fun addToFavourites(article: News.Articles?) {
        favouriteArticles = favouritesLiveData.value
        article?.let { favouriteArticles?.add(it) }
        favouritesLiveData.postValue(favouriteArticles)
    }

    /**
     * Method to remove news article from favourites
     */
    fun removeFromFavourites(article: News.Articles?) {
        favouriteArticles = favouritesLiveData.value
        favouriteArticles?.remove(article)
        favouritesLiveData.postValue(favouriteArticles)
    }

    /**
     * Method to check news article in favourites set
     */
    fun isFavouriteNews(article: News.Articles?): Boolean? {
        return favouritesLiveData.value?.contains(article)
    }

    /**
     * Method to return set of favourites
     */
    fun getFavourites(): MutableSet<News.Articles>? {
        favouriteArticles = favouritesLiveData.value
        return favouriteArticles
    }

}
