package com.example.demoapp.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.demoapp.R
import com.example.demoapp.adapter.PageAdapter
import com.example.demoapp.fragments.NewsFragment
import com.example.demoapp.utils.addFragment
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
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager>(R.id.dashboard_viewpager)

        // Set the adapter for each tab item
        val pageAdapter = PageAdapter(supportFragmentManager, tabLayout.tabCount)
        with(viewPager) {
            this.adapter = pageAdapter
            this.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        }


        // Listen to each tab item to set the fragment
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let { viewPager.currentItem = it.position }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logout_option) {
            showAlert()
            true
        } else {
            false
        }
    }

    /*
     * Function to show alert dialog box after button click.
     */
    private fun showAlert() {
        val builder = AlertDialog.Builder(this, R.style.DialogBoxTheme)
        builder.setTitle("Logout")
        builder.setMessage("Do you really want to logout?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { _, _ ->
            startActivity(Intent(this, MainActivity::class.java))
            getSharedPreferences("app-userInfo", Context.MODE_PRIVATE).edit().clear().apply()
            finish()
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}