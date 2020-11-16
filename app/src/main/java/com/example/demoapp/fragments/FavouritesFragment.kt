package com.example.demoapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demoapp.R
import com.example.demoapp.adapter.FavouritesAdapter
import com.example.demoapp.viewmodels.NewsViewModel


/**
 * A simple [Fragment] subclass for Favourites
 */
class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val newsViewModel = activity?.let { ViewModelProvider(it).get(NewsViewModel::class.java) }

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_view)
        loadRecyclerView(recyclerView, newsViewModel)

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
     * Method to invoke layoutManager and recyclerViewAdapter
     */
    private fun loadRecyclerView(recyclerView: RecyclerView, newsViewModel: NewsViewModel?) {
        val articles = newsViewModel?.newsLiveData?.value
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = FavouritesAdapter(articles) { item ->
                newsViewModel?.newsLiveData?.value?.remove(item)
            }
            adapter?.notifyDataSetChanged()
            setHasFixedSize(true)
        }
    }

}