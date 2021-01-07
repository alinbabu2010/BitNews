package com.example.demoapp.api

import com.example.demoapp.BuildConfig
import com.example.demoapp.models.News
import com.example.demoapp.utils.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton class to create and use Retrofit service
 */
object RetrofitManager {

    // To initialize and build an retrofit variable
    val getRetrofit: Retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    // To provide retrofit service using RetrofitAPI interface
    private val getAPIService: RetrofitAPI = getRetrofit.create(RetrofitAPI::class.java)

    private const val sortBy = "popularity"
    private const val keyWord = "bitcoin"
    private const val numberOfResult = 10

    /**
     * Method to call retrofit service using [getAPIService] and get APIResponse from [loadData]
     */
    fun getNewsData( page : Int ,resource: (APIResponse<News>) -> Unit ){
        val call = getAPIService.getNews(BuildConfig.API_KEY, keyWord,sortBy,page,numberOfResult)
        loadData(call){
            resource(it)
        }
    }

    /**
     * Method to fetch news from API
     */
    private fun <T : Any> loadData(call: Call<T>, apiResponse: (APIResponse<T>) -> Unit) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                Resource.loading(response)
                if (response.isSuccessful) apiResponse(APIResponse.Success(response.body()))
                else {
                    apiResponse(APIResponse.Error(ErrorUtils().parseError(response)))
                }
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                apiResponse(APIResponse.Failure(t))
            }
        })
    }
}