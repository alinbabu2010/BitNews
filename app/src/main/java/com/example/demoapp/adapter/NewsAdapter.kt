package com.example.demoapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.activities.ArticleActivity
import com.example.demoapp.fragments.NewsFragment
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import java.text.SimpleDateFormat
import java.util.*


/**
 * Adapter class for RecyclerView of [NewsFragment]
 */
class NewsAdapter(
    private val news: News,
    private val articles: MutableLiveData<ArrayList<Articles>>,
    private val listener: (Articles) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    /**
     * A subclass for providing a reference to the views for each data item
     */
    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsImage: ImageView = view.findViewById(R.id.news_image)
        val newsTitle: TextView = view.findViewById(R.id.news_title)
        val newsDesc: TextView = view.findViewById(R.id.news_desc)
        val newsSrc: TextView = view.findViewById(R.id.source_textview)
        val newsAuthor: TextView = view.findViewById(R.id.author_textview)
        val newsDate: TextView = view.findViewById(R.id.publish_textview)
        val newsLiked: RadioButton = view.findViewById(R.id.favourites_button)
        val context: Context = view.context
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.news_item,
            parent,
            false
        )
        return NewsViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view from news ArrayList (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = news.articles[position]
        Glide.with(holder.context).load(item.urlToImage).override(800).into(holder.newsImage)
        holder.newsTitle.text = item.title
        holder.newsDesc.text = item.description

        val author = "Author: ${item.author}"
        holder.newsAuthor.text = author

        val src = "Source: ${item.source.name}"
        holder.newsSrc.text = src

        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(item.publishedAt)
        val formattedDate =
            date?.let { SimpleDateFormat("MMM dd, y hh:mm a", Locale.US).format(it) }

        val publishDate = "Published on $formattedDate"
        holder.newsDate.text = publishDate

        holder.newsLiked.isChecked = articles.value?.contains(item) ?: false

        holder.newsLiked.setOnClickListener {
            listener(item)
            notifyDataSetChanged()
        }

        holder.newsImage.setOnClickListener {
            openArticle(holder.context,position)
        }

        holder.newsTitle.setOnClickListener {
           openArticle(holder.context,position)
        }
    }

    /**
     * Return the size of your data set (invoked by the layout manager)
     */
    override fun getItemCount(): Int = news.articles.size

    /**
     * Method to start news details activity
     */
    private fun openArticle(context: Context, position: Int) {
        val intent = Intent(context, ArticleActivity::class.java)
        intent.putExtra("position",position)
        startActivity(context,intent, Bundle.EMPTY)
    }
}