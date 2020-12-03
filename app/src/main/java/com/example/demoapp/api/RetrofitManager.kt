package com.example.demoapp.api

import com.example.demoapp.utils.ErrorUtils
import com.example.demoapp.utils.NETWORK_ERROR_MESSAGE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


object RetrofitManager {

    /**
     * Method to initialize and build an retrofit object
     */
    fun getRetrofit(baseURL: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Method to create retrofit service using RetrofitAPI interface
     */
    fun getRetrofitService(baseURL: String): RetrofitAPI {
        val retrofit = getRetrofit(baseURL)
        return retrofit.create(RetrofitAPI::class.java)
    }

    /**
     * Method to fetch news from API
     */
    fun <T : Any> loadData(call: Call<T>, baseURL: String , apiResponse: (Resource<T?>) -> Unit) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                Resource.loading(response)
                if (response.isSuccessful) apiResponse(Resource.success(APIResponse.Success(response.body()).data))
                else {
                    val apiError = APIResponse.Error<T>(ErrorUtils().parseError(response, baseURL))
                    val errorMessage = "${apiError.error?.status()} - ${apiError.error?.message()}"
                    apiResponse(Resource.error(null,errorMessage))
                }
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                if (t is IOException) apiResponse(Resource.error(null, NETWORK_ERROR_MESSAGE))
                else apiResponse(Resource.error(null,t.message.toString()))
            }
        })
    }
}