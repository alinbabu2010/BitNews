package com.example.demoapp.api

import com.example.demoapp.utils.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
                    apiResponse(Resource.error(null,apiError.error))
                }
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                apiResponse(Resource.failure(null,t))
            }
        })
    }
}