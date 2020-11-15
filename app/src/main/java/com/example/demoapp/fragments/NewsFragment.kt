package com.example.demoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp.adapter.NewsAdapter
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.viewmodels.NewsViewModel
import com.google.gson.GsonBuilder


/**
 * A simple [Fragment] subclass for displaying news
 */
class NewsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflatedView = inflater.inflate(R.layout.fragment_news, container, false)
        val newsViewModel = activity?.let { ViewModelProvider(it).get(NewsViewModel::class.java) }

        // Call the function to parse JSON file.
        val news = loadJSONFromAsset()

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView = inflatedView.findViewById<RecyclerView>(R.id.recycler_view)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = newsViewModel?.let {
                NewsAdapter(news, it) { item ->
                    newsViewModel.newsLiveData.value?.add(item)
                    println(newsViewModel.newsLiveData.value)
                }
            }
            setHasFixedSize(true)
        }

        return inflatedView
    }

    /**
     * Method to get the news from JSON file and add it to news ArrayList
     */
    private fun loadJSONFromAsset(): News {
        lateinit var news: News
        val gson = GsonBuilder().setPrettyPrinting().create()
        val fileData: String? = activity?.assets?.open("news.json")?.readBytes()?.let { String(it) }
        gson.fromJson(fileData, News::class.java).let { news = it }
        return news
    }

}