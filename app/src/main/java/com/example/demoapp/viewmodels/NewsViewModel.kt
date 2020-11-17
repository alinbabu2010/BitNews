package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.models.Articles

/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel : ViewModel() {

    var newsLiveData = MutableLiveData<MutableSet<Articles>>()

    init {
        newsLiveData.value = mutableSetOf()
    }

    fun addNews(articles: Articles): Boolean? {
        return newsLiveData.value?.add(articles)
    }

    fun removeNews(articles: Articles) {
        newsLiveData.value?.remove(articles)
    }

}
