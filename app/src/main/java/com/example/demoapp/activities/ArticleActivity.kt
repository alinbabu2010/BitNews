package com.example.demoapp.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.models.News
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass for view each news article.
 */
class ArticleActivity : AppCompatActivity() {

    /**
     * This method creates the options menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val position = intent.getIntExtra("position",1)
        val newsImage: ImageView = findViewById(R.id.appbar_news_image)
        val newsTitle: TextView = findViewById(R.id.news_title2)
        val newsDesc: TextView = findViewById(R.id.news_desc2)
        val newsSrc: TextView = findViewById(R.id.source_textview2)
        val newsAuthor: TextView = findViewById(R.id.author_textview2)
        val newsDate: TextView = findViewById(R.id.publish_textview2)
        val toolbar:Toolbar? = findViewById(R.id.toolbar)

        val news = loadJSONFromAsset()
        val articles = position.let { news.articles[it] }

        Glide.with(applicationContext).load(articles.urlToImage).override(800).into(newsImage)

        newsTitle.text = articles.title
        newsDesc.text = articles.content

        val author = "Author: ${articles.author}"
        newsAuthor.text = author

        val src = "Source: ${articles.source.name}"
        newsSrc.text = src

        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(articles.publishedAt)
        val formattedDate =
            date?.let { SimpleDateFormat("MMM dd, y hh:mm a", Locale.US).format(it) }

        val publishDate = "Published on $formattedDate"
        newsDate.text = publishDate

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbarLayout.title = articles.title
        collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(getColor(R.color.design_default_color_error)))
    }

    /**
     * Method to get the news from JSON file and add it to news ArrayList
     */
    private fun loadJSONFromAsset(): News {
        lateinit var news: News
        val gson = GsonBuilder().setPrettyPrinting().create()
        val fileData: String? = assets?.open("news.json")?.readBytes()?.let { String(it) }
        gson.fromJson(fileData, News::class.java).let { news = it }
        return news
    }
}