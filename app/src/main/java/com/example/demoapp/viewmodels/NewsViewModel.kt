package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.models.Articles

/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel() : ViewModel() {

    val news: MutableLiveData<ArrayList<Articles>> by lazy{
        MutableLiveData<ArrayList<Articles>>()
    }

}
