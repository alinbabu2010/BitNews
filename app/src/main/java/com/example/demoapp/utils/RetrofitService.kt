package com.example.demoapp.utils

import com.example.demoapp.api.UserPostAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    fun getService(baseURL : String): UserPostAPI {
        val retrofit = getRetrofit(baseURL)
        return retrofit.create(UserPostAPI::class.java)
    }

    fun getRetrofit(baseURL: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}