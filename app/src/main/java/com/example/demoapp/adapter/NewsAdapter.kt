package com.example.demoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.ui.activities.fragments.dashboard.NewsFragment
import com.example.demoapp.models.Articles
import com.example.demoapp.utils.Const.Companion.DATE_FORMAT_DECODE
import com.example.demoapp.utils.Const.Companion.DATE_FORMAT_ENCODE
import com.example.demoapp.utils.Utils.Companion.openArticle
import com.example.demoapp.utils.Utils.Companion.openDummyActivity
import com.example.demoapp.utils.Utils.Companion.shareNews
import com.example.demoapp.viewmodels.NewsViewModel
import java.text.SimpleDateFormat
import java.util.*


/**
 * Adapter class for RecyclerView of [NewsFragment]
 */
class NewsAdapter(
    private val news: ArrayList<Articles>?,
    private val newsViewModel: NewsViewModel?,
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
        val shareNews: ImageButton = view.findViewById(R.id.share_button)
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
        val item = news?.get(position)
        Glide.with(holder.context).load(item?.imageUrl).override(800).into(holder.newsImage)
        holder.newsTitle.text = item?.title.toString()
        holder.newsDesc.text = item?.description.toString()

        val author = "Author: ${item?.author}"
        holder.newsAuthor.text = author

        val src = "Source: ${item?.source?.name}"
        holder.newsSrc.text = src

        val date = SimpleDateFormat(
            DATE_FORMAT_ENCODE,
            Locale.US
        ).parse(item?.publishedDate.toString())
        val formattedDate =
            date?.let { SimpleDateFormat(DATE_FORMAT_DECODE, Locale.US).format(it) }

        val publishDate = "Published on $formattedDate"
        holder.newsDate.text = publishDate

        holder.newsLiked.isChecked = newsViewModel?.isFavouriteNews(item) == true

        holder.newsLiked.setOnClickListener {
            newsViewModel?.addToFavourites(item)
            notifyDataSetChanged()
        }

        holder.newsImage.setOnClickListener {
            openDummyActivity(holder.context, item)
        }

        holder.newsTitle.setOnClickListener {
            openArticle(holder.context, item)
        }

        holder.shareNews.setOnClickListener {
            shareNews(holder.context, item?.url)
        }
    }

    /**
     * Return the size of your data set (invoked by the layout manager)
     */
    override fun getItemCount(): Int = news?.size ?: 0


}