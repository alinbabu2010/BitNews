package com.example.demoapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.models.News
import com.example.demoapp.models.UserPost
import kotlinx.android.synthetic.main.activity_dummy.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        button_get.setOnClickListener {
            loadNews()
        }

        button_post.setOnClickListener {
            saveData()
            //thread.start()
        }

    }


    /**
     * Method to make a GET request using retrofit
     */
    private fun loadNews() {

        // Retrofit builder
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Object to call methods
        val newsAPI: NewsAPI = retrofit.create(NewsAPI::class.java)
        val call: Call<News> = newsAPI.getNews()
        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news: News? = response.body()
                val stringBuilder = StringBuilder()
                news?.articles?.forEach {
                    with(stringBuilder) {
                        append(it.title)
                        append("\n")
                        append(it.author)
                        append("\n")
                        append(it.content)
                        append("\n")
                        append(it.description)
                        append("\n")
                        append(it.publishedAt)
                        append("\n")
                        append(it.url)
                        append("\n\n")
                    }
                }

                textView_out.text = stringBuilder
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Toast.makeText(baseContext, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Method to make a POST request using retrofit
     */
    private fun saveData() {

        // Retrofit builder
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Object to call methods
        val newsAPI: NewsAPI = retrofit.create(NewsAPI::class.java)
        val newPost = UserPost(1, 2, "Sample Title", "Hello my dear friend!")
        val callPost: Call<UserPost> = newsAPI.setPost(newPost)

        // Asynchronous method
        callPost.enqueue(object : Callback<UserPost> {
            override fun onResponse(call: Call<UserPost>, response: Response<UserPost>) {
                val message = " ${response.code()} \n ${response.raw()} \n" + " ${response.body()}"
                textView_out.text = message
            }

            override fun onFailure(call: Call<UserPost>, t: Throwable) {
                textView_out.text = t.message.toString()
            }
        })


    }


    // Thread to execute Synchronous method of retrofit POST request
    private val thread = Thread() {

        // Retrofit builder
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Object to call methods
        val newsAPI: NewsAPI = retrofit.create(NewsAPI::class.java)
        val newPost = UserPost(1, 2, "Sample Title", "Hello my dear friend!")
        val callPost: Call<UserPost> = newsAPI.setPost(newPost)

        val post = callPost.execute()
        val message = " ${post.code()} \n ${post.message()}"
        runOnUiThread{ textView_out.text = message }
    }

}
