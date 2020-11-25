package com.example.demoapp.activities

import com.example.demoapp.models.News
import retrofit2.Call
import retrofit2.http.GET

interface NewsAPI {

    // http://newsapi.org/v2/  top-headlines?sources=techcrunch&apiKey=19b71eb781b0404b93feafc1badf4324

    @GET("top-headlines?sources=techcrunch&apiKey=19b71eb781b0404b93feafc1badf4324")
    fun getNews() : Call<News>

}