package com.example.demoapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.demoapp.api.Resource
import com.example.demoapp.database.ArticlesDatabase
import com.example.demoapp.firebase.FavoritesFirebase.retrieveDataFromFirebase
import com.example.demoapp.firebase.FavoritesFirebase.storeDataOnFirebase
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.repository.ArticleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * This class is used to define view model for favourite news storing
 * @param application Parameter to use as application base context
 */
class NewsViewModel(application: Application) : AndroidViewModel(application) {

    var pageCount = 1
    var isLoading = false
    private val articlesDAO = ArticlesDatabase.getDatabase(application).articlesDAO()
    private val repository = ArticleRepository(articlesDAO)
    val newsLiveData = MutableLiveData<Resource<News?>>()
    val favouriteArticles: MutableSet<Articles> = mutableSetOf()
    val favouritesLiveData: MutableLiveData<MutableSet<Articles>> by lazy {
        MutableLiveData<MutableSet<Articles>>()
    }
    var articles = MutableLiveData<List<Articles>>()

    /**
     * Method to get the articles from database
     * Post the list of articles to [NewsViewModel.articles] live data
     */
    fun getArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.readArticles {
                articles.postValue(it)
            }
        }
    }

    /**
     * Method to get the news from API url
     * @param page To denote the API page count
     */
    fun getNews(page: Int) {
        repository.getArticles(page) {
            newsLiveData.postValue(it)
        }
    }

    /**
     * Method to add news article to favourites
     * @param article An object of class [Articles]
     */
    fun addToFavourites(article: Articles?) {
        favouritesLiveData.value?.let { favouriteArticles.addAll(it) }
        article?.let { favouriteArticles.add(it) }
        storeOnFirebase(favouriteArticles)
    }

    /**
     * Method to remove news article from favourites
     * @param article An object of class [Articles]
     */
    fun removeFromFavourites(article: Articles?) {
        favouritesLiveData.value?.let { favouriteArticles.addAll(it) }
        favouriteArticles.remove(article)
        storeOnFirebase(favouriteArticles)
    }

    /**
     * Method to save favourites on firebase
     * @param favouriteArticles Mutable set of class [Articles]
     */
    private fun storeOnFirebase(favouriteArticles: MutableSet<Articles>?) {
        storeDataOnFirebase(favouriteArticles) {
            if (it) favouritesLiveData.postValue(favouriteArticles)
        }
    }

    /**
     * Method to check news article in favourites set
     * @param article An object of class [Articles]
     * @return [Boolean] specifying is the given [article] is added to favorites or not
     */
    fun isFavouriteNews(article: Articles?): Boolean {
        var isFavourite = false
        favouritesLiveData.value?.forEach {
            if (it.title == article?.title) isFavourite = true
        }
        return isFavourite
    }

    /**
     * Method to get favourites articles from firebase
     */
    fun getFavourites() {
        retrieveDataFromFirebase {
            favouritesLiveData.postValue(it)
        }
        favouritesLiveData.value?.let { favouriteArticles.addAll(it) }
    }

    /**
     * Method to add articles to room database
     * @param article An object of class [Articles]
     */
    fun addArticles(article: List<Articles>) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.addArticles(article)
        }
    }

}
