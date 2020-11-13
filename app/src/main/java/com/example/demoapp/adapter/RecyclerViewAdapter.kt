package com.example.demoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.fragments.NewsFragment
import com.example.demoapp.models.News
import com.example.demoapp.viewmodels.DemoViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter class for RecyclerView of [NewsFragment]
 */
class RecyclerViewAdapter(
    private val news: News,
    private val favourite: DemoViewModel
) : RecyclerView.Adapter<RecyclerViewAdapter.NewsViewHolder>(){


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

        if (favourite.favouriteNews.value.isNullOrEmpty()){
            favourite.favouriteNews.value = arrayListOf()
        }

        holder.newsLiked.isChecked = favourite.isFavourites(item) == true

        holder.newsLiked.setOnClickListener {
            favourite.addFavourites(item)
        }
    }

    /**
     * Return the size of your data set (invoked by the layout manager)
     */
    override fun getItemCount(): Int = news.articles.size

}