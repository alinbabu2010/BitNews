package com.example.demoapp.viewmodels

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
 * This class is used to define view model for favourite news storing
 */
class NewsViewModel : ViewModel() {

    var newsLiveData = MutableLiveData<MutableSet<Articles>>()
    var newsArticles = ArrayList<Articles>()
    private var articles: MutableSet<Articles>? = mutableSetOf()

    init {
        newsLiveData.value = mutableSetOf()
    }

    /**
     * Method to get the news from JSON file
     */
    fun getNews(activity: Activity): ArrayList<Articles> {
        val newsAPI = RetrofitService.getNewsService("http://newsapi.org/v2/")
        val call: Call<News> = newsAPI.getNews()
        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                if (response.isSuccessful) {
                    newsArticles = response.body()?.articles ?: arrayListOf()
                } else {
                    val error: APIError? = ErrorUtils().parseError(response)
                    Toast.makeText(
                        activity.applicationContext,
                        error?.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                if (t is IOException) {
                    Toast.makeText(
                        activity.baseContext,
                        "Network failure or Not Found",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(
                        activity.baseContext,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
        return newsArticles
    }

    /**
     * Method to add news article to favourites
     */
    fun addToFavourites(article: Articles?) {
        articles = newsLiveData.value
        article?.let { articles?.add(it) }
        newsLiveData.postValue(articles)
    }

    /**
     * Method to remove news article from favourites
     */
    fun removeFromFavourites(article: Articles?) {
        articles = newsLiveData.value
        articles?.remove(article)
        newsLiveData.postValue(articles)
    }

    /**
     * Method to check news article in favourites set
     */
    fun isFavouriteNews(article: Articles?): Boolean? {
        return newsLiveData.value?.contains(article)
    }

    /**
     * Method to return set of favourites
     */
    fun getFavourites(): MutableSet<Articles>? {
        articles = newsLiveData.value
        return articles
    }

}
