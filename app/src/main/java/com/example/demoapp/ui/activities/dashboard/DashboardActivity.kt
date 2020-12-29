package com.example.demoapp.ui.activities.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.demoapp.R
import com.example.demoapp.adapter.PageAdapter
import com.example.demoapp.ui.activities.fragments.dashboard.NewsFragment
import com.example.demoapp.utils.Utils.Companion.addFragment
import com.example.demoapp.utils.Utils.Companion.showAlert
import com.example.demoapp.viewmodels.AccountsViewModel
import com.google.android.material.tabs.TabLayout


/**
 * Activity class for dashboard of logged user
 */

class DashboardActivity : AppCompatActivity() {

    /**
     * This method creates the options menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        addFragment(NewsFragment(), R.id.dashboard_viewpager, supportFragmentManager)
        addTabLayout()
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
        val pageAdapter = PageAdapter(supportFragmentManager, tabLayout.tabCount,tabTitles)
        viewPager.adapter = pageAdapter
        tabLayout.TabView(this)
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0..tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.icon = ContextCompat.getDrawable(this, image[i])
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logout_option) {
            val accountsViewModel = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
            showAlert(this, this,accountsViewModel)
            true
        } else {
            false
        }
    }

}