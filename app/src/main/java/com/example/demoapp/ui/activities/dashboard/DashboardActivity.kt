package com.example.demoapp.ui.activities.dashboard

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.work.*
import com.example.demoapp.R
import com.example.demoapp.adapter.PageAdapter
import com.example.demoapp.ui.fragments.dashboard.NewsFragment
import com.example.demoapp.utils.Constants.Companion.FAIL_MSG
import com.example.demoapp.utils.Utils.Companion.addFragment
import com.example.demoapp.utils.Utils.Companion.openChat
import com.example.demoapp.utils.Utils.Companion.showAlert
import com.example.demoapp.viewmodels.AccountsViewModel
import com.example.demoapp.viewmodels.NewsViewModel
import com.example.demoapp.workers.NewsWorker
import com.example.demoapp.workers.NewsWorker.Companion.WORK_NAME
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import java.time.Duration


/**
 * Activity class for dashboard of logged user
 */

class DashboardActivity : AppCompatActivity() {

    private var newsViewModel: NewsViewModel? = null

    /**
     * Overriding [onCreateOptionsMenu] to create the options menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        addFragment(NewsFragment(), R.id.dashboard_viewpager, supportFragmentManager)
        addTabLayout()
        getFirebaseToken()
        setupRecurringWork()
    }

    /**
     * Method to get firebase messaging token
     */
    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i(TAG,FAIL_MSG, task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d(TAG,token)
        })
    }

    /**
     * Method to add tab layout to the activity
     */
    private fun addTabLayout() {
        val image =
            intArrayOf(R.drawable.ic_news, R.drawable.ic_favourite_outlined, R.drawable.ic_profile)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.dashboard_viewpager)
        val tabTitles = arrayOf(
            getString(R.string.news_title),
            getString(R.string.favourite_title),
            getString(R.string.profile_text)
        )

        // Set the adapter for each tab item
        val pageAdapter = PageAdapter(supportFragmentManager, tabLayout.tabCount, tabTitles)
        viewPager.adapter = pageAdapter
        tabLayout.TabView(this)
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0..tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.icon = ContextCompat.getDrawable(this, image[i])
        }
    }

    /**
     * Setup WorkManager background job to 'fetch' new network data daily.
     */
    private fun setupRecurringWork() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()
        val task = PeriodicWorkRequestBuilder<NewsWorker>(Duration.ofMinutes(15)).setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            task)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(task.id).observe(this,{ info ->
            if (info != null && info.state.isFinished) {
                newsViewModel?.getArticles()
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.logout_option -> {
                val accountsViewModel = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
                showAlert(this, this, accountsViewModel)
                true
            }
            R.id.chat_option -> {
                openChat(this)
                true
            }
            else -> false
        }
    }

    companion object
    {
        private const val TAG = "DashboardActivity"
    }
}