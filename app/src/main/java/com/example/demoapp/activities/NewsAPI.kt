package com.example.demoapp.activities

import com.example.demoapp.models.News
import com.example.demoapp.models.UserPost
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NewsAPI {

    // http://newsapi.org/v2/  top-headlines?sources=techcrunch&apiKey=19b71eb781b0404b93feafc1badf4324

    @GET("top-headlines?sources=techcrunch&apiKey=19b71eb781b0404b93feafc1badf4324")
    fun getNews() : Call<News>

    //https://jsonplaceholder.typicode.com/   posts

    @POST("posts")
    fun setPost( @Body userPost: UserPost) : Call<UserPost>

}