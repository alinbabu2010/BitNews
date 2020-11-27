package com.example.demoapp.api

import android.app.Activity
import android.widget.Toast
import com.example.demoapp.models.APIError
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.utils.ErrorUtils
import com.example.demoapp.utils.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

/**
 * Class to get news from API and store as a news instance
 */
class NewsRepository {

    private val newsAPI = RetrofitService.getNewsService("http://newsapi.org/v2/")
    var news = ArrayList<Articles>()

    /**
     * Method to fetch news from API
     */
    fun loadNews(activity: Activity) {
        val call: Call<News> = newsAPI.getNews()
        try {

            val response: Response<News> = call.execute()
            if (response.isSuccessful) {
                news = response.body()?.articles ?: arrayListOf()
            } else {
                val error: APIError? = ErrorUtils().parseError(response)
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        activity.applicationContext,
                        error?.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: IOException) {
            GlobalScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    activity.applicationContext,
                    "Network failure",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}