package com.example.demoapp.utils

import com.example.demoapp.api.NewsAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    fun getRetrofit(baseURL: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getNewsService(baseURL : String): NewsAPI {
        val retrofit = getRetrofit(baseURL)
        return retrofit.create(NewsAPI::class.java)
    }
}