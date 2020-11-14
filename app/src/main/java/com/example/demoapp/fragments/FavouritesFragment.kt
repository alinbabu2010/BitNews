package com.example.demoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demoapp.R
import com.example.demoapp.adapter.LikeAdapter
import com.example.demoapp.models.Articles
import com.example.demoapp.viewmodels.NewsViewModel


/**
 * A simple [Fragment] subclass for Favourites
 */
class FavouritesFragment : Fragment() {

    private var articles = MutableLiveData<ArrayList<Articles>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inflatedView =  inflater.inflate(R.layout.fragment_favourites, container, false)


        val newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        newsViewModel.news.observeForever {
            articles.value = it
        }

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView = inflatedView.findViewById<RecyclerView>(R.id.recycler_view)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = LikeAdapter(articles,newsViewModel)
            setHasFixedSize(true)
        }

        return  inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swiprRefresh)
        refreshLayout.setOnRefreshListener {
            onCreate(savedInstanceState)
            refreshLayout.isRefreshing = false
        }
    }

}