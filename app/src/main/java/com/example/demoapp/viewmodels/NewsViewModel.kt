package com.example.demoapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.models.Articles

/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel : ViewModel() {

    private var _newsLiveData = MutableLiveData<ArrayList<Articles>>()
    val newsLiveData : LiveData<ArrayList<Articles>>
    get() = _newsLiveData

    init {
        _newsLiveData.value = arrayListOf()
    }

    fun addNews(articles: Articles){
        newsLiveData.value?.add(articles)
    }

    fun removeNews(articles: Articles){
        newsLiveData.value?.remove(articles)
    }

    fun isFavourite(articles: Articles): Boolean? {
        return newsLiveData.value?.contains(articles)
    }

}
