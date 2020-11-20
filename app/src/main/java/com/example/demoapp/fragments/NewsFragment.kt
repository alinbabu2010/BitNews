package com.example.demoapp.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demoapp.R
import com.example.demoapp.adapter.NewsAdapter
import com.example.demoapp.models.Articles
import com.example.demoapp.viewmodels.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A simple [Fragment] subclass for displaying news
 */
class NewsFragment : Fragment() {

    private var newsViewModel: NewsViewModel? = null
    private var container: ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.container = container
        return inflater.inflate(R.layout.fragment_news, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = activity?.let { ViewModelProvider(it).get(NewsViewModel::class.java) }
        val articles = activity?.let { newsViewModel?.getNews(it) }

        // Getting recyclerView and invoke layoutManager and recyclerViewAdapter
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = NewsAdapter(articles, newsViewModel)
            setHasFixedSize(true)
        }
        newsViewModel?.newsLiveData?.observe(viewLifecycleOwner, {
            recyclerView.adapter?.notifyDataSetChanged()
            onCreate(savedInstanceState)
        })

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
            setBottomSheetDialog(recyclerView, articles)
        }

    }

    /**
     * Method to set radio buttons and datepicker for filter bottom sheet dialog and process the filter clicks
     */
    private fun setBottomSheetDialog(recyclerView: RecyclerView, article: ArrayList<Articles>?) {

        val source = mutableSetOf<String>()
        val bottomSheet = context?.let { it1 -> BottomSheetDialog(it1) }
        val bottomSheetView: View = layoutInflater.inflate(R.layout.news_filter, container, false)
        bottomSheet?.setContentView(bottomSheetView)
        bottomSheet?.show()

        val sourceRadioGroup: RadioGroup = bottomSheetView.findViewById(R.id.source_radioGroup)
        val dateView: TextView = bottomSheetView.findViewById(R.id.publish_datepicker)
        article?.forEach { source.add(it.source.name) }

        // Adding radio button based on the source set
        for (i in source.indices) {
            val radioButton = RadioButton(context)
            radioButton.text = source.toTypedArray()[i]
            radioButton.id = View.generateViewId()
            sourceRadioGroup.addView(radioButton)
        }

        // Initializing data picker dialog and inflating date picker on clicking
        val date: Calendar = Calendar.getInstance()
        val datePicker =
            context?.let {
                DatePickerDialog(
                    it,
                    { _, year, month, dayOfMonth ->
                        val updateDate = Calendar.getInstance()
                        updateDate.set(year, month, dayOfMonth)
                        val month1 = month + 1
                        val day: String =
                            if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                        val dateToSet = "$year-$month1-$day"
                        dateView.text = dateToSet
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                )
            }
        dateView.setOnClickListener { datePicker?.show() }

        val applyButton: Button = bottomSheetView.findViewById(R.id.apply_button)
        applyButton.setOnClickListener {
            filterNews(bottomSheet, dateView, sourceRadioGroup, article, recyclerView)
        }

        val clearButton: Button = bottomSheetView.findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
            dateView.text = null
            sourceRadioGroup.clearCheck()
            view?.findViewById<TextView>(R.id.no_matching_textView)?.visibility = View.INVISIBLE
            recyclerView.swapAdapter(NewsAdapter(article, newsViewModel), false)
        }

    }


    /**
     * Method to filter the news according to source or published date
     */
    private fun filterNews(
        bottomSheet: BottomSheetDialog?,
        dateView: TextView,
        sourceRadioGroup: RadioGroup,
        article: ArrayList<Articles>?,
        recyclerView: RecyclerView
    ) {
        val checkedRadio =
            bottomSheet?.findViewById<RadioButton>(sourceRadioGroup.checkedRadioButtonId)
        var sourceName: String? = null
        if (checkedRadio != null) {
            sourceName = checkedRadio.text as String?
        }
        val publishedDate = dateView.text
        Log.i("Filter", sourceName.toString())
        if (sourceName.isNullOrEmpty() && publishedDate.isNullOrEmpty()) {
            Toast.makeText(context, "Select at least one filter", Toast.LENGTH_SHORT).show()
        } else {
            val sourceFilter =
                article?.filter { it.source.name == sourceName.toString() } as ArrayList<Articles>
            var dateFilter = arrayListOf<Articles>()
            if (publishedDate.isNotEmpty()) {
                dateFilter =
                    article.filter { it.publishedAt.startsWith(publishedDate.toString()) } as ArrayList<Articles>
            }
            Log.i("date", dateFilter.toString())
            val distinctNewsFilter: ArrayList<Articles>
            distinctNewsFilter = when {
                (sourceFilter.isEmpty()) -> dateFilter
                (dateFilter.isEmpty()) -> sourceFilter
                else -> (sourceFilter + dateFilter).distinct() as ArrayList<Articles>
            }
            Log.i("final", distinctNewsFilter.toString())
            if (distinctNewsFilter.isEmpty()) {
                view?.findViewById<TextView>(R.id.no_matching_textView)?.visibility = View.VISIBLE
                recyclerView.swapAdapter(NewsAdapter(null, newsViewModel), false)
                bottomSheet?.dismiss()
            } else {
                recyclerView.swapAdapter(NewsAdapter(distinctNewsFilter, newsViewModel), false)
                bottomSheet?.dismiss()
            }

        }
    }

}
