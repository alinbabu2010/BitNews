package com.example.demoapp.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.models.News
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

    }

    fun loadNews(view: View) {

        // Retrofit builder
        val retrofit = Retrofit.Builder()
            .baseUrl("http://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Object to call methods
        val newsAPI = retrofit.create(NewsAPI::class.java)

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

                view.findViewById<TextView>(R.id.textView_out).text = stringBuilder
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Toast.makeText(baseContext, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

}
