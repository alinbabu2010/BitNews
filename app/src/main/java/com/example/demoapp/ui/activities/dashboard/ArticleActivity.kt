package com.example.demoapp.ui.activities.dashboard

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.models.Articles
import com.example.demoapp.utils.Const.Companion.DATE_FORMAT_DECODE
import com.example.demoapp.utils.Const.Companion.DATE_FORMAT_ENCODE
import com.example.demoapp.utils.Utils.Companion.showAlert
import com.example.demoapp.viewmodels.AccountsViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.text.SimpleDateFormat
import java.util.*


/**
 * An activity for viewing each news article.
 */
class ArticleActivity : AppCompatActivity() {

    /**
     * Overriding [onCreateOptionsMenu] to create the options menu
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

        val article = intent.getParcelableExtra<Articles>("article")
        val newsImage: ImageView = findViewById(R.id.appbar_news_image)
        val newsDesc: TextView = findViewById(R.id.news_desc2)
        val newsSrc: TextView = findViewById(R.id.source_text_display)
        val newsAuthor: TextView = findViewById(R.id.author_text_display)
        val newsDate: TextView = findViewById(R.id.publish_date_display)
        val newsContent: TextView = findViewById(R.id.news_content)
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        val openNews: Button = findViewById(R.id.open_button)

        Glide.with(applicationContext).load(article?.imageUrl).override(800).into(newsImage)
        newsDesc.text = article?.description.toString()
        newsContent.text = article?.content.toString()

        val author = "Author: ${article?.author}"
        newsAuthor.text = author

        val src = "Source: ${article?.source?.name}"
        newsSrc.text = src

        val date =
            SimpleDateFormat(
                DATE_FORMAT_ENCODE,
                Locale.US
            ).parse(article?.publishedDate.toString())
        val formattedDate =
            date?.let { SimpleDateFormat(DATE_FORMAT_DECODE, Locale.US).format(it) }

        val publishDate = "Published on $formattedDate"
        newsDate.text = publishDate

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbarLayout.title = article?.title.toString()
        collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(getColor(R.color.secondary_dark)))

        // Open the news in browser on button click
        openNews.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(article?.url.toString())
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logout_option) {
            val accountsViewModel = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
            showAlert(this, this, accountsViewModel)
            true
        } else {
            false
        }
    }

}