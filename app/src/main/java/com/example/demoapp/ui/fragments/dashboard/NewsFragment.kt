package com.example.demoapp.ui.fragments.dashboard

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp.adapter.NewsAdapter
import com.example.demoapp.api.Resource
import com.example.demoapp.databinding.FragmentNewsBinding
import com.example.demoapp.models.Articles
import com.example.demoapp.models.News
import com.example.demoapp.receivers.NotificationReceiver
import com.example.demoapp.ui.activities.dashboard.DashboardActivity
import com.example.demoapp.utils.Constants.Companion.GROUP_KEY
import com.example.demoapp.utils.Constants.Companion.MAX_RESULTS
import com.example.demoapp.utils.Constants.Companion.NOTIFICATION_ID
import com.example.demoapp.viewmodels.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar


/**
 * A simple [Fragment] subclass for displaying news
 */
class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private var newsViewModel: NewsViewModel? = null
    private var container: ViewGroup? = null
    private var checkedRadio: RadioButton? = null
    private var publishedDate: String? = null
    private var articles: ArrayList<Articles>? = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this.container = container
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        binding.loadMoreProgressBar.visibility = View.GONE
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = activity?.let { ViewModelProviders.of(it).get(NewsViewModel::class.java) }
        val recyclerView: RecyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        newsViewModel?.getArticles()
        newsViewModel?.getFavourites()

        newsViewModel?.newsLiveData?.observe(viewLifecycleOwner) {
            checkNewsStatus(recyclerView, it)
        }

        newsViewModel?.articles?.observe(viewLifecycleOwner) {
            setNewsData(it, recyclerView)
        }

        newsViewModel?.favouritesLiveData?.observe(viewLifecycleOwner) {
            recyclerView.adapter?.notifyDataSetChanged()
            onCreate(savedInstanceState)
        }

        with(binding.swipeRefresh) {
            setProgressBackgroundColorSchemeColor(Color.YELLOW)
            setColorSchemeResources(R.color.secondary_dark)
        }

        // Refresh on swipe by calling recycler view
        binding.swipeRefresh.setOnRefreshListener {
            setRefreshState()
        }

        // Inflate bottom sheet dialog on floating action button click
        binding.filterButton.setOnClickListener {
            setBottomSheetDialog(articles)
        }

        // Setting up to load more news on last news item
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    loadMoreNews(layoutManager)
                }
            }
        })
    }

    /**
     * Method to check news api state and perform operation according to each state
     * @param recyclerView An instance of [RecyclerView] of the [NewsFragment]
     * @param resource An instance of [Resource] for news response
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun checkNewsStatus(recyclerView: RecyclerView, resource: Resource<News?>) {
        when (resource.status) {
            Resource.Status.SUCCESS -> {
                binding.progressBarNews.visibility = View.GONE
                articles = resource.data?.articles
                articles?.let { newsViewModel?.addArticles(it) }
                newsViewModel?.getArticles()
                newsViewModel?.newsLiveData?.postValue(Resource.finished())
            }
            Resource.Status.ERROR -> {
                binding.loadMoreProgressBar.visibility = View.GONE
                binding.progressBarNews.visibility = View.GONE
                Toast.makeText(activity, resource.message, Toast.LENGTH_LONG).show()
                newsViewModel?.newsLiveData?.postValue(Resource.finished())
            }
            Resource.Status.LOADING -> {
                binding.progressBarNews.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            Resource.Status.REFRESHING -> {
                newsViewModel?.pageCount?.let { newsViewModel?.getNews(it) }
                newsViewModel?.isLoading = true
                notifyUser()
            }
            Resource.Status.LOAD_MORE -> {
                binding.loadMoreProgressBar.visibility = View.GONE
                resource.data?.articles?.let { articles?.addAll(it) }
                recyclerView.adapter?.notifyDataSetChanged()
                newsViewModel?.isLoading = false
            }
            Resource.Status.FINISHED -> {
                newsViewModel?.isLoading = false
                if (binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    /**
     * Method to set data in news fragment
     * @param articleList List of [Articles] to be loaded
     * @param recyclerView [RecyclerView] to be set
     */
    private fun setNewsData(articleList: List<Articles>?, recyclerView: RecyclerView) {
        if (newsViewModel?.articles?.value.isNullOrEmpty()) {
            val count = newsViewModel?.pageCount as Int
            newsViewModel?.getNews(count)
        } else {
            articles = articleList as ArrayList<Articles>
            binding.progressBarNews.visibility = View.INVISIBLE
            setRecyclerView(recyclerView)
        }
    }

    /**
     * Method to set the refresh state on swipe to refresh
     */
    private fun setRefreshState() {
        newsViewModel?.pageCount = 1
        val state = Resource.refreshing<News>()
        newsViewModel?.newsLiveData?.postValue(state)
    }

    /**
     * Method to set data in recyclerview
     * @param recyclerView An instance of [RecyclerView]
     */
    private fun setRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = NewsAdapter(articles, newsViewModel)
        recyclerView.setHasFixedSize(true)
        binding.progressBarNews.visibility = View.GONE
    }

    /**
     * Method to load more news om recycler view item end
     * @param layoutManager is used to access recycler view layout manager
     */
    private fun loadMoreNews(layoutManager: LinearLayoutManager) {
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
        if (newsViewModel?.isLoading != true && lastVisibleItem == totalItemCount - 1) {
            if (totalItemCount >= MAX_RESULTS) {
                Snackbar.make(binding.recyclerView, "No more news", Snackbar.LENGTH_SHORT).show()
            } else {
                newsViewModel?.pageCount = newsViewModel?.pageCount as Int + 1
                binding.loadMoreProgressBar.visibility = View.VISIBLE
                newsViewModel?.isLoading = true
                newsViewModel?.pageCount?.let { newsViewModel?.getNews(it) }
            }
        }
    }

    /**
     * Method to set radio buttons and datepicker for filter bottom sheet dialog and process the filter clicks
     * @param article An [ArrayList] of [Articles] instance.
     */
    private fun setBottomSheetDialog(article: ArrayList<Articles>?) {

        val source = mutableSetOf<String>()
        val bottomSheet = context?.let { it1 -> BottomSheetDialog(it1) }
        val bottomSheetView: View = layoutInflater.inflate(R.layout.news_filter, container, false)
        bottomSheet?.setContentView(bottomSheetView)
        bottomSheet?.show()

        val sourceRadioGroup: RadioGroup = bottomSheetView.findViewById(R.id.source_radioGroup)
        val dateView: TextView = bottomSheetView.findViewById(R.id.publish_datepicker)
        article?.forEach { it.source?.name?.let { it1 -> source.add(it1) } }

        // Adding radio button based on the source set
        for (i in source.indices) {
            val radioButton = RadioButton(context)
            radioButton.text = source.toTypedArray()[i]
            radioButton.id = View.generateViewId()
            sourceRadioGroup.addView(radioButton)
            if (checkedRadio?.text == radioButton.text) {
                sourceRadioGroup.check(radioButton.id)
            }
        }
        dateView.text = publishedDate

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
        dateView.setOnClickListener {
            datePicker?.show()
            if (publishedDate?.isNotEmpty() == true) {
                publishedDate?.substring(0, 4)?.toInt()?.let { year ->
                    publishedDate?.substring(5, 7)?.toInt()?.let { month ->
                        publishedDate?.substringAfterLast("-")?.toInt()?.let { day ->
                            datePicker?.updateDate(year, month - 1, day)
                        }
                    }
                }
            }
        }

        val applyButton: Button = bottomSheetView.findViewById(R.id.apply_button)
        applyButton.setOnClickListener {
            filterNews(bottomSheet, dateView, sourceRadioGroup, article)
        }

        val clearButton: Button = bottomSheetView.findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
            dateView.text = null
            sourceRadioGroup.clearCheck()
            checkedRadio = null
            publishedDate = null
            view?.findViewById<TextView>(R.id.no_matching_textView)?.visibility = View.INVISIBLE
            binding.recyclerView.swapAdapter(NewsAdapter(article, newsViewModel), false)
        }

    }


    /**
     * Method to filter the news according to source or published date
     * @param bottomSheet An instance of [BottomSheetDialog] class
     * @param dateView An instance to access [TextView] for date display.
     * @param sourceRadioGroup An instance of [RadioGroup]
     * @param article An [ArrayList] of [Articles] instance.
     */
    private fun filterNews(
        bottomSheet: BottomSheetDialog?,
        dateView: TextView,
        sourceRadioGroup: RadioGroup,
        article: ArrayList<Articles>?
    ) {
        checkedRadio = bottomSheet?.findViewById(sourceRadioGroup.checkedRadioButtonId)
        val sourceName = checkedRadio?.text
        publishedDate = dateView.text as String?

        if (sourceName.isNullOrEmpty() && publishedDate.isNullOrEmpty()) {
            Toast.makeText(context, R.string.empty_filter_text, Toast.LENGTH_SHORT).show()
        } else {
            val sourceFilter =
                article?.filter { it.source?.name == sourceName.toString() } as ArrayList<Articles>?
            var dateFilter: ArrayList<Articles>? = arrayListOf()
            if (publishedDate?.isNotEmpty() == true) {
                dateFilter =
                    article?.filter { it.publishedDate?.equals(publishedDate.toString()) == true } as ArrayList<Articles>?
            }
            val distinctNewsFilter: ArrayList<Articles>? = when {
                (sourceFilter?.isEmpty() == true) -> dateFilter
                (publishedDate?.isEmpty() == true) -> sourceFilter
                else -> dateFilter?.filter { it.source?.name == sourceName.toString() } as ArrayList<Articles>?
            }
            if (distinctNewsFilter?.isEmpty() == true) {
                view?.findViewById<TextView>(R.id.no_matching_textView)?.visibility = View.VISIBLE
                binding.recyclerView.swapAdapter(NewsAdapter(null, newsViewModel), false)
                bottomSheet?.dismiss()
            } else {
                view?.findViewById<TextView>(R.id.no_matching_textView)?.visibility = View.INVISIBLE
                binding.recyclerView.swapAdapter(
                    NewsAdapter(distinctNewsFilter, newsViewModel),
                    false
                )
                bottomSheet?.dismiss()
            }

        }
    }

    /**
     * Method to notify user about news update
     */
    private fun notifyUser() {
        var pendingIntent: PendingIntent? = null
        var clearPendingIntent: PendingIntent? = null
        createChannel()
        val intent = Intent(this.activity, DashboardActivity::class.java)
        pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val clearIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = Intent.ACTION_DELETE
            putExtra("notificationId", NOTIFICATION_ID)
        }
        clearPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            clearIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = context?.let { NotificationCompat.Builder(it, EVENT_CHANNEL_ID) }
            ?.setSmallIcon(R.drawable.ic_stat_notification)
            ?.setContentTitle(getString(R.string.channel_title))
            ?.setContentText(getString(R.string.channel_text))
            ?.setColor(Color.GREEN)
            ?.setContentIntent(pendingIntent)
            ?.setGroup(GROUP_KEY)
            ?.setOnlyAlertOnce(true)
            ?.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            ?.setCategory(NotificationCompat.CATEGORY_MESSAGE)
            ?.addAction(
                R.drawable.quantum_ic_clear_grey600_24,
                getString(R.string.clear),
                clearPendingIntent
            )
        val notificationManagerCompat = context?.let { NotificationManagerCompat.from(it) }
        notificationBuilder?.let { notificationManagerCompat?.notify(NOTIFICATION_ID, it.build()) }
    }

    /**
     * Method to create notification channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channelName = getString(R.string.channel_name)
        val channel = NotificationChannel(
            EVENT_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.channel_description)
        channel.shouldShowLights()
        val notificationManager = context?.let {
            getSystemService(
                it,
                NotificationManager::class.java
            )
        }
        notificationManager?.createNotificationChannel(channel)
    }

    companion object {
        private const val EVENT_CHANNEL_ID = "EVENT_CHANNEL_ID"
    }
}
