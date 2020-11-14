package com.example.demoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.models.Articles
import com.example.demoapp.viewmodels.NewsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Adapter class for RecyclerView of FavouritesFragment
 */
class LikeAdapter(
    val news: LiveData<ArrayList<Articles>>,
    val newsViewModel: NewsViewModel
) : RecyclerView.Adapter<LikeAdapter.LikeViewHolder>(){


    /**
     * A subclass for providing a reference to the views for each data item
     */
    class LikeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.favourite_item,
            parent,
            false
        )
        return LikeViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view from news ArrayList (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: LikeViewHolder, position: Int) {
        val item = news.value?.get(position)
        Glide.with(holder.context).load(item?.urlToImage).override(800).into(holder.newsImage)
        holder.newsTitle.text = item?.title
        holder.newsDesc.text = item?.description

        val author = "Author: ${item?.author}"
        holder.newsAuthor.text = author

        val src = "Source: ${item?.source?.name}"
        holder.newsSrc.text = src

        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(item?.publishedAt.toString())
        val formattedDate =
            date?.let { SimpleDateFormat("MMM dd, y hh:mm a", Locale.US).format(it) }

        val publishDate = "Published on $formattedDate"
        holder.newsDate.text = publishDate

        holder.newsLiked.isChecked = news.value?.contains(item)   == true

        holder.newsLiked.setOnClickListener {
            news.value?.remove(item)
            notifyDataSetChanged()
        }

    }

    /**
     * Return the size of your data set (invoked by the layout manager)
     */
    override fun getItemCount(): Int = news.value?.size ?: 0

}