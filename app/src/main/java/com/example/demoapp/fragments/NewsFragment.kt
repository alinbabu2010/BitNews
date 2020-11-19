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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demoapp.R
import com.example.demoapp.adapter.NewsAdapter
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.utils.loadJSONFromAsset
import com.example.demoapp.viewmodels.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A simple [Fragment] subclass for displaying news
 */
class NewsFragment : Fragment() {

    private var newsViewModel: NewsViewModel? = null
    private var container: ViewGroup? = null
    val articles: MutableLiveData<MutableSet<Articles>> = MutableLiveData(mutableSetOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.container = container
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
        newsViewModel?.newsLiveData?.observe(viewLifecycleOwner, {
            articles.value = it
            recyclerView.adapter?.notifyDataSetChanged()
            onCreate(savedInstanceState)
        })
        setRecyclerViewAdapter(recyclerView,news,articles)

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
            val bottomSheet = context?.let { it1 -> BottomSheetDialog(it1) }
            val bottomSheetView: View = layoutInflater.inflate(R.layout.news_filter, container,false)
            bottomSheet?.setContentView(bottomSheetView)
            bottomSheet?.show()
            val sourceRadioGroup: RadioGroup = bottomSheetView.findViewById(R.id.source_radioGroup)
            val dateView: TextView = bottomSheetView.findViewById(R.id.publish_datepicker)
            setBottomSheetDialog(news, sourceRadioGroup, dateView)

            val applyButton: Button = bottomSheetView.findViewById(R.id.apply_button)
            applyButton.setOnClickListener {
                filterNews(bottomSheet,dateView,sourceRadioGroup,news,recyclerView)
            }

            val clearButton : Button = bottomSheetView.findViewById(R.id.clear_button)
            clearButton.setOnClickListener {
                dateView.text = null
                sourceRadioGroup.clearCheck()
                setRecyclerViewAdapter(recyclerView,news,articles)
            }
        }

    }

    /**
     * Method to set radio buttons and datepicker for filter bottom sheet dialog
     */
    private fun setBottomSheetDialog(
        news: News,
        sourceRadioGroup: RadioGroup,
        dateView: TextView
    ) {
        val article = news.articles
        val source = mutableSetOf<String>()
        article.forEach { source.add(it.source.name) }

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
                        val day : String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                        val dateToSet = "$year-$month1-$day"
                        dateView.text = dateToSet
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                )
            }
        dateView.setOnClickListener { datePicker?.show() }

    }

    /**
     * Method to set the recycler view adapter
     */
    private fun setRecyclerViewAdapter(recyclerView: RecyclerView, news:News, articles: MutableLiveData<MutableSet<Articles>>){
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
    }

    /**
     * Method to filter the news according to source or published date
     */
    private fun filterNews(
        bottomSheet: BottomSheetDialog?,
        dateView: TextView,
        sourceRadioGroup: RadioGroup,
        news: News,
        recyclerView: RecyclerView
    ) {
        val article = news.articles
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
            val sourceFilter  = article.filter { it.source.name == sourceName.toString() } as ArrayList<Articles>
            var dateFilter = arrayListOf<Articles>()
            if (publishedDate.isNotEmpty()) {
                dateFilter = article.filter { it.publishedAt.startsWith(publishedDate.toString()) } as ArrayList<Articles>
            }
            Log.i("date", dateFilter.toString())
            val distinctNewsFilter : ArrayList<Articles>
            distinctNewsFilter = when {
                (sourceFilter.isEmpty()) ->  dateFilter
                (dateFilter.isEmpty()) ->  sourceFilter
                else -> (sourceFilter + dateFilter).distinct() as ArrayList<Articles>
            }
            Log.i("final", distinctNewsFilter.toString())
            if (distinctNewsFilter.isEmpty()) {
                Toast.makeText(context, "No matching news", Toast.LENGTH_SHORT).show()
            } else {
                val newsFiltered = News(distinctNewsFilter)
                setRecyclerViewAdapter(recyclerView,newsFiltered,articles)
            }
            bottomSheet?.dismiss()
        }
    }

}
