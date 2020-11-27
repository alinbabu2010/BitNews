package com.example.demoapp.api

import com.example.demoapp.models.UserPost
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * API interface for each posts
 */
interface UserPostAPI {

    @GET("posts/100")
    fun getPost() : Call<UserPost>

    @POST("posts")
    fun setPost( @Body userPost: UserPost) : Call<UserPost>

    @Multipart
    @POST("photos")
    fun uploadPhoto(@Part("description") description:RequestBody, @Part  file: MultipartBody.Part) : Call<ResponseBody>
}