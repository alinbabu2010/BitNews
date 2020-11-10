package com.example.demoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp.adapter.RecyclerViewAdapter
import com.example.demoapp.models.News
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


/**
 * A simple [Fragment] subclass for displaying news
 */
class NewsFragment : Fragment() {
    var news: ArrayList<News> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.actionBar?.setDisplayShowTitleEnabled(true)
        activity?.title = "News"

        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_news, container, false)

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView = inflatedView.findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = RecyclerViewAdapter(news)
        recyclerView.setHasFixedSize(true)

        // Call the function to parse JSON file.
        loadJSONFromAsset()

        return inflatedView
    }

    /**
     * Method to get the news from JSON file and add it to news ArrayList
     */
    private fun loadJSONFromAsset() {
        try {
            val input: InputStream? = activity?.assets?.open("news.json")
            val size: Int? = input?.available()
            val buffer = size?.let { ByteArray(it) }
            input?.read(buffer)
            input?.close()
            val json = JSONObject(String(buffer!!))
            val jsonArray:JSONArray = json.getJSONArray("articles")
            for (i in 0 until jsonArray.length()) {
                val newsDetail = jsonArray.getJSONObject(i)
                news.add(News(newsDetail.getString("urlToImage"), newsDetail.getString("title"), newsDetail.getString("description")))
            }
        } catch (ex: IOException) { ex.printStackTrace() }
    }

}