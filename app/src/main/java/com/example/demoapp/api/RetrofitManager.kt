package com.example.demoapp.api

import androidx.lifecycle.MutableLiveData
import com.example.demoapp.models.APIError
import com.example.demoapp.utils.ErrorUtils
import com.example.demoapp.utils.NETWORK_ERROR_MESSAGE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


object RetrofitManager {

    val toastMessageObserver: MutableLiveData<String?> = MutableLiveData()

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
    fun <T> loadData(call: Call<T>, baseURL: String, articles: (T) -> Unit) {
        val apiResponse = APIResponse<T>()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) response.body()?.let { apiResponse.success(it) }?.value?.let { articles(it) }
                else {
                    val error: APIError? = ErrorUtils().parseError(response, baseURL)
                    toastMessageObserver.value = apiResponse.error(error)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (t is IOException) toastMessageObserver.value = NETWORK_ERROR_MESSAGE
                else toastMessageObserver.value = t.message.toString()
            }
        })
    }
}