package com.example.demoapp.api

import com.example.demoapp.models.News
import retrofit2.Call
import retrofit2.http.GET

/**
 * API interface for news
 */
interface NewsAPI {

    @GET("everything?q=bitcoin&apiKey=$API_KEY")
    fun getNews(): Call<News>

    companion object {
        const val API_KEY = "19b71eb781b0404b93feafc1badf4324"
    }
}