package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.api.APIResponse
import com.example.demoapp.api.Resource
import com.example.demoapp.api.RetrofitManager
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
class NewsViewModel : ViewModel() {

    val newsLiveData = MutableLiveData<Resource<News?>>()

    var favouritesLiveData = MutableLiveData<MutableSet<Articles>>()
    private var favouriteArticles: MutableSet<Articles>? = mutableSetOf()

    init {
        favouritesLiveData.value = mutableSetOf()
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
        article?.let { favouriteArticles?.add(it) }
        storeOnFirebase()
    }

    /**
     * Method to remove news article from favourites
     */
    fun removeFromFavourites(article: Articles?) {
        favouriteArticles?.remove(article)
        storeOnFirebase()
    }

    /**
     * Method to save favourites on firbase
     */
    private fun storeOnFirebase(){
        CoroutineScope(Dispatchers.IO).launch {
            storeDataOnFirebase(favouriteArticles){
                if(it) favouritesLiveData.postValue(favouriteArticles)
            }
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
    fun getFavourites(): MutableSet<Articles>? {
        CoroutineScope(Dispatchers.IO).launch {
            retrieveDataFromFirebase {
                favouritesLiveData.postValue(it)
            }
        }
        favouriteArticles = favouritesLiveData.value
        return favouriteArticles
    }

}
