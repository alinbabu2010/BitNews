package com.example.demoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private  val TAG = "RecyclerViewAdapter"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val newsImage = itemView.findViewById<ImageView>(R.id.news_image)
        val newsTitle= itemView.findViewById<ImageView>(R.id.news_title)
        val newsDesc = itemView.findViewById<ImageView>(R.id.news_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }
}