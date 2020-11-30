package com.example.demoapp.api

import android.app.Activity
import android.widget.Toast
import com.example.demoapp.models.APIError
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.utils.ErrorUtils
import com.example.demoapp.utils.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Class to get news from API and store as a news instance
 */
class NewsRepository {

    private val newsAPI = RetrofitService.getNewsService("http://newsapi.org/v2/")

    /**
     * Method to fetch news from API
     */
    fun loadNews(activity: Activity, articles: (ArrayList<Articles>) -> Unit) {
        val call: Call<News> = newsAPI.getNews()
        call.enqueue(object : Callback<News>{
            override fun onResponse(call: Call<News>, response: Response<News>) {
                if (response.isSuccessful) response.body()?.articles?.let { articles(it) }
                else {
                    val error: APIError? = ErrorUtils().parseError(response)
                    Toast.makeText(
                        activity.applicationContext,
                        error?.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                if( t is IOException ) {
                    Toast.makeText(
                        activity.applicationContext,
                        "Network failure",
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