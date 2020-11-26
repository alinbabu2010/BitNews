package com.example.demoapp.utils

import com.example.demoapp.api.NewsAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Services {

    fun getService(baseURL : String): NewsAPI {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(NewsAPI::class.java)
    }
}