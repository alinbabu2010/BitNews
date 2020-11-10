package com.example.demoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp.fragments.NewsFragment
import com.example.demoapp.models.News
import com.squareup.picasso.Picasso

/**
 * Adapter class for RecyclerView of [NewsFragment]
 */
class RecyclerViewAdapter(
    private val news: ArrayList<News>,
) : RecyclerView.Adapter<RecyclerViewAdapter.NewsViewHolder>() {

    /**
     * A subclass for providing a reference to the views for each data item
     */
    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val newsImage: ImageView = view.findViewById(R.id.news_image)
        val newsTitle: TextView = view.findViewById(R.id.news_title)
        val newsDesc: TextView = view.findViewById(R.id.news_desc)
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder{
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
        val item = news[position]
        Picasso.get().load(item.newsImage).fit().into(holder.newsImage)
        holder.newsTitle.text = item.newsTitle
        holder.newsDesc.text = item.newsDesc
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount(): Int {
        return news.size
    }

}