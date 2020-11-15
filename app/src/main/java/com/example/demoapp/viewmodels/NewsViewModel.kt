package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.models.Articles

/**
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel() : ViewModel() {

    var newsLiveData: MutableLiveData<ArrayList<Articles>> = MutableLiveData(arrayListOf())

}
