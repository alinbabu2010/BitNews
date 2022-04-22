package com.example.demoapp.api

import com.example.demoapp.models.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * API interface for news
 */
interface RetrofitAPI {

    @GET("everything")
    fun getNews(
        @Header("Authorization") key: String,
        @Query("q") topic: String,
        @Query("sortBy") sortBy: String,
        @Query("page") page: Int,
        @Query("pageSize") size: Int
    ): Call<News>

}