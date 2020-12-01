package com.example.demoapp.api

import android.app.Activity
import android.widget.Toast
import com.example.demoapp.models.APIError
import com.example.demoapp.utils.ErrorUtils
import com.example.demoapp.utils.NETWORK_ERROR_MESSAGE
import com.example.demoapp.utils.NOT_FOUND
import com.example.demoapp.utils.SERVER_ERROR
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
    fun getRetrofitService(baseURL : String): RetrofitAPI {
        val retrofit = getRetrofit(baseURL)
        return retrofit.create(RetrofitAPI::class.java)
    }

    /**
     * Method to fetch news from API
     */
    fun <T> loadNews(activity: Activity, call: Call<T>,baseURL: String, articles: (T) -> Unit) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) response.body()?.let { articles(it) }
                else {
                    val error: APIError? = ErrorUtils().parseError(response,baseURL)
                    when (error?.status()){
                        404 -> Toast.makeText(activity.applicationContext, NOT_FOUND, Toast.LENGTH_SHORT).show()
                        500 -> Toast.makeText(activity.applicationContext, SERVER_ERROR, Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(activity.applicationContext, error?.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if( t is IOException) {
                    Toast.makeText(
                        activity.applicationContext,
                        NETWORK_ERROR_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        activity.applicationContext,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }
}
