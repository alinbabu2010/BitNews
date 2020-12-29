package com.example.demoapp.ui.activities.fragments.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demoapp.R
import com.example.demoapp.adapter.FavouritesAdapter
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.example.demoapp.viewmodels.NewsViewModel


/**
 * A simple [Fragment] subclass for Favourites
 */
class FavouritesFragment : Fragment() {

    private var newsViewModel: NewsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        newsViewModel = activity?.let { ViewModelProviders.of(it).get(NewsViewModel::class.java) }

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView: RecyclerView = view.findViewById(R.id.fragment_view)
        loadRecyclerView(recyclerView, newsViewModel)

        newsViewModel?.favouritesLiveData?.observe(viewLifecycleOwner, {
            val articles = newsViewModel?.favouriteArticles
            if (firebaseError!=null) Toast.makeText(context, firebaseError,Toast.LENGTH_SHORT).show()
            if (articles.isNullOrEmpty()) {
                view.findViewById<TextView>(R.id.empty_textView)?.visibility = View.VISIBLE
            } else {
                view.findViewById<TextView>(R.id.empty_textView)?.visibility = View.INVISIBLE
            }
            recyclerView.adapter?.notifyDataSetChanged()
        })

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
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = FavouritesAdapter(newsViewModel)
            adapter?.notifyDataSetChanged()
            setHasFixedSize(true)
        }
    }

}