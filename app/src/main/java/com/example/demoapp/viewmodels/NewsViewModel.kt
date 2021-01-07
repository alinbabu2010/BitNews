package com.example.demoapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demoapp.api.Resource
import com.example.demoapp.database.ArticlesDatabase
import com.example.demoapp.firebase.FavoritesFirebase.Companion.retrieveDataFromFirebase
import com.example.demoapp.firebase.FavoritesFirebase.Companion.storeDataOnFirebase
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.repository.ArticleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel(application: Application) : AndroidViewModel(application) {

    var page = 1
    var isLoading = false
    val maxPageLimit = 10
    private val repository : ArticleRepository
    var articles : LiveData<List<Articles>>
    var newsLiveData = MutableLiveData<Resource<News?>>()

    val favouritesLiveData : MutableLiveData<MutableSet<Articles>> by lazy {
        MutableLiveData<MutableSet<Articles>>()
    }
    val favouriteArticles: MutableSet<Articles> = mutableSetOf()

    init {
        val articlesDAO = ArticlesDatabase.getDatabase(application).articlesDAO()
        repository = ArticleRepository(articlesDAO)
        articles = repository.readArticles()
    }

    /**
     * Method to get the news from API url
     */
    fun getNews(page : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getArticles(page)
            newsLiveData = repository.newsLiveData
        }
    }

    /**
     * Method to add news article to favourites
     */
    fun addToFavourites(article: Articles?) {
        favouritesLiveData.value?.let { favouriteArticles.addAll(it) }
        article?.let { favouriteArticles.add(it) }
        storeOnFirebase(favouriteArticles)
    }

    /**
     * Method to remove news article from favourites
     */
    fun removeFromFavourites(article: Articles?) {
        favouritesLiveData.value?.let { favouriteArticles.addAll(it) }
        favouriteArticles.remove(article)
        storeOnFirebase(favouriteArticles)
    }

    /**
     * Method to save favourites on firebase
     */
    private fun storeOnFirebase(favouriteArticles: MutableSet<Articles>?) {
        storeDataOnFirebase(favouriteArticles) {
            if (it) favouritesLiveData.postValue(favouriteArticles)
        }
    }

    /**
     * Method to check news article in favourites set
     */
    fun isFavouriteNews(article: Articles?): Boolean {
        var isFavourite = false
        favouritesLiveData.value?.forEach{
            if(it.title == article?.title) isFavourite = true
        }
        return isFavourite
    }

    /**
     * Method to return set of favourites
     */
    fun getFavourites() {
        retrieveDataFromFirebase {
            favouritesLiveData.postValue(it)
        }
        favouritesLiveData.value?.let { favouriteArticles.addAll(it) }
    }

    /**
     * Method to add articles to room database
     */
    fun addArticles(article: Articles) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.addArticles(article)
        }
    }

}
