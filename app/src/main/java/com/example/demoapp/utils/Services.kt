package com.example.demoapp.utils

import com.example.demoapp.api.PostsAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Services {

    fun getService(baseURL : String): PostsAPI {
        val retrofit = getRetrofit(baseURL)
        return retrofit.create(PostsAPI::class.java)
    }

    fun getRetrofit(baseURL: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}