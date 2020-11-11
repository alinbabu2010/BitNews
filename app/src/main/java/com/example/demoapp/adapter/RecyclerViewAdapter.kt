package com.example.demoapp.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp.fragments.NewsFragment
import com.example.demoapp.models.News
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Adapter class for RecyclerView of [NewsFragment]
 */
class RecyclerViewAdapter(
    private val news: News,
) : RecyclerView.Adapter<RecyclerViewAdapter.NewsViewHolder>() {

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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = news.articles[position]
        Picasso.get().load(item.urlToImage).fit().into(holder.newsImage)
        holder.newsTitle.text = item.title
        holder.newsDesc.text = item.description

        val author = "Author: " + item.author
        holder.newsAuthor.text = author

        val src = "Source: " + item.source.name
        holder.newsSrc.text = src

        val ldt: LocalDateTime = LocalDateTime.parse(
            item.publishedAt,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        )
        val currentZoneId: ZoneId = ZoneId.systemDefault()
        val currentZonedDateTime: ZonedDateTime = ldt.atZone(currentZoneId)
        val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val publishDate =
            "Published on " + format.format(currentZonedDateTime) + " at " + ldt.toLocalTime()
        holder.newsDate.text = publishDate
    }

    /**
     * Return the size of your data set (invoked by the layout manager)
     */
    override fun getItemCount(): Int = news.articles.size
}