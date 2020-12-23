package com.example.demoapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demoapp.api.APIResponse
import com.example.demoapp.api.Resource
import com.example.demoapp.api.RetrofitManager
import com.example.demoapp.database.ArticleRepository
import com.example.demoapp.database.ArticlesDatabase
import com.example.demoapp.firebase.FirebaseOperations.Companion.retrieveDataFromFirebase
import com.example.demoapp.firebase.FirebaseOperations.Companion.storeDataOnFirebase
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : ArticleRepository
    val allArticles : LiveData<List<Articles>>
    val newsLiveData = MutableLiveData<Resource<News?>>()

    var favouritesLiveData = MutableLiveData<MutableSet<Articles>>()
    var favouriteArticles: MutableSet<Articles>? = mutableSetOf()

    init {
        val articlesDAO = ArticlesDatabase.getDatabase(application).articlesDAO()
        repository = ArticleRepository(articlesDAO)
        allArticles = repository.readAllData
    }

    /**
     * Method to get the news from API url
     */
    fun getNews() {
        RetrofitManager.getRetrofitService {
            when (it) {
                is APIResponse.Success -> newsLiveData.postValue(Resource.success(it.data))
                is APIResponse.Error -> newsLiveData.postValue(Resource.error(it.error))
                is APIResponse.Failure -> newsLiveData.postValue(Resource.failure(it.exception))
            }
        }
    }

    /**
     * Method to add news article to favourites
     */
    fun addToFavourites(article: Articles?) {
        favouriteArticles = favouritesLiveData.value
        article?.let { favouriteArticles?.add(it) }
        storeOnFirebase()
    }

    /**
     * Method to remove news article from favourites
     */
    fun removeFromFavourites(article: Articles?) {
        favouriteArticles = favouritesLiveData.value
        favouriteArticles?.remove(article)
        storeOnFirebase()
    }

    /**
     * Method to save favourites on firebase
     */
    private fun storeOnFirebase() {
        storeDataOnFirebase(favouriteArticles) {
            if (it) favouritesLiveData.postValue(favouriteArticles)
        }
    }

    /**
     * Method to check news article in favourites set
     */
    fun isFavouriteNews(article: Articles?): Boolean? {
        return favouritesLiveData.value?.contains(article)
    }

    /**
     * Method to return set of favourites
     */
    fun getFavourites() {
        retrieveDataFromFirebase {
            favouritesLiveData.postValue(it)
        }
        favouriteArticles = favouritesLiveData.value
    }

    fun addArticles(article: Articles) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.addArticles(article)
        }
    }

}
