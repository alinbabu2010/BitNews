package com.example.demoapp.api

import com.example.demoapp.models.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API interface for news
 */
interface RetrofitAPI {

    @GET("everything")
    fun getNews(
        @Query("q") topic : String,
        @Query("apiKey") key: String
    ): Call<News>

}