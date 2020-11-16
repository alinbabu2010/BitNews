package com.example.demoapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

    private lateinit var news: News
    private var newsViewModel: NewsViewModel? = null
    private lateinit var articles: MutableLiveData<ArrayList<Articles>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_news, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call the function to parse JSON file.
        news = loadJSONFromAsset()
        newsViewModel = activity?.let { ViewModelProvider(it).get(NewsViewModel::class.java) }

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView : RecyclerView = view.findViewById(R.id.recycler_view)
        articles = MutableLiveData(arrayListOf())
        newsViewModel?.newsLiveData?.observe(viewLifecycleOwner, {
            articles.value = it
            recyclerView.adapter?.notifyDataSetChanged()
            onCreate(savedInstanceState)
        })
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = NewsAdapter(news,articles) { item ->
                if (newsViewModel?.isFavourite(item) == false) {
                    newsViewModel?.addNews(item)
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
                }
            }
            adapter?.notifyDataSetChanged()
            setHasFixedSize(true)
        }

        // Refresh on swipe by calling recycler view
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        refreshLayout.setProgressBackgroundColorSchemeColor(Color.YELLOW)
        refreshLayout.setColorSchemeResources(R.color.secondary_dark)
        refreshLayout.setOnRefreshListener {
            recyclerView.adapter?.notifyDataSetChanged()
            refreshLayout.isRefreshing = false
        }
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        val recyclerView : RecyclerView? = view?.findViewById(R.id.recycler_view)
        if (isVisibleToUser && recyclerView?.isInLayout==false) {
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}
