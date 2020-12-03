package com.example.demoapp.api

import com.example.demoapp.models.News
import com.example.demoapp.utils.Const.Companion.API_KEY
import retrofit2.Call
import retrofit2.http.GET

/**
 * API interface for news
 */
interface RetrofitAPI {

    @GET("everything?q=bitcoin&apiKey=$API_KEY")
    fun getNews(): Call<News>

}