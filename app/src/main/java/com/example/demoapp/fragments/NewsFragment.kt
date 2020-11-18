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
import com.example.demoapp.utils.loadJSONFromAsset
import com.example.demoapp.viewmodels.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A simple [Fragment] subclass for displaying news
 */
class NewsFragment : Fragment() {

    private var newsViewModel: NewsViewModel? = null
    private lateinit var articles: MutableLiveData<MutableSet<Articles>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_news, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call the function to parse JSON file.
        val fileData: String? = activity?.assets?.open("news.json")?.readBytes()?.let { String(it) }
        val news = loadJSONFromAsset(fileData)
        newsViewModel = activity?.let { ViewModelProvider(it).get(NewsViewModel::class.java) }

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        articles = MutableLiveData(mutableSetOf())
        newsViewModel?.newsLiveData?.observe(viewLifecycleOwner, {
            articles.value = it
            recyclerView.adapter?.notifyDataSetChanged()
            onCreate(savedInstanceState)
        })
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = NewsAdapter(news, articles) { item ->
                if (articles.value?.add(item) == true) {
                    newsViewModel?.newsLiveData?.postValue(articles.value)
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

        // Inflate bottom sheet dialog on floating action button click
        val filter: FloatingActionButton = view.findViewById(R.id.filter_button)
        filter.setOnClickListener {
            val bottomSheet = FilterDialogFragment()
            activity?.supportFragmentManager?.let { it1 ->
                bottomSheet.show(
                    it1,
                    "FilterBottomSheet"
                )
            }
        }
    }

}
