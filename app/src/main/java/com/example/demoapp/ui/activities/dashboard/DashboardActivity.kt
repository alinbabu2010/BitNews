package com.example.demoapp.ui.activities.dashboard

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.work.*
import com.example.demoapp.R
import com.example.demoapp.adapter.PageAdapter
import com.example.demoapp.utils.Constants.Companion.FAIL_MSG
import com.example.demoapp.utils.DashboardTabs
import com.example.demoapp.utils.Utils.Companion.openChat
import com.example.demoapp.utils.Utils.Companion.showAlert
import com.example.demoapp.viewmodels.AccountsViewModel
import com.example.demoapp.viewmodels.NewsViewModel
import com.example.demoapp.workers.NewsWorker
import com.example.demoapp.workers.NewsWorker.Companion.WORK_NAME
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
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
                Log.i(TAG, FAIL_MSG, task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, token)
        })
    }

    /**
     * Method to add tab layout to the activity
     */
    private fun addTabLayout() {

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = findViewById(R.id.dashboard_viewpager)

        // Set the adapter for each tab item
        val pageAdapter =
            PageAdapter(supportFragmentManager, this.lifecycle, DashboardTabs.values().size)
        viewPager.adapter = pageAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                DashboardTabs.NEWS.ordinal -> {
                    tab.text = getString(R.string.news_title)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_news)
                }
                DashboardTabs.FAVOURITES.ordinal -> {
                    tab.text = getString(R.string.favourite_title)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_favourite_outlined)
                }
                DashboardTabs.PROFILE.ordinal -> {
                    tab.text = getString(R.string.profile_text)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_profile)
                }
            }
        }.attach()

    }

    /**
     * Setup WorkManager background job to 'fetch' new network data daily.
     */
    private fun setupRecurringWork() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true).build()
        val task = PeriodicWorkRequestBuilder<NewsWorker>(Duration.ofMinutes(15)).setConstraints(
            constraints
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            task
        )
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(task.id).observe(this) { info ->
            if (info != null && info.state.isFinished) {
                newsViewModel?.getArticles()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_option -> {
                val accountsViewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)
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

    companion object {
        private const val TAG = "DashboardActivity"
    }
}