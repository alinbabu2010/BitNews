package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.models.Articles

/**
 * This class is used to define view model for favourite news storing
 */
class DemoViewModel : ViewModel() {


    val favouriteNews = MutableLiveData<ArrayList<Articles>>()

    fun addFavourites(article: Articles){
        favouriteNews.value?.add(article)
    }

    fun removeFavourites(article: Articles){
        favouriteNews.value?.remove(article)
    }

    fun isFavourites(article: Articles): Boolean? {
        return favouriteNews.value?.contains(article)
    }

}
