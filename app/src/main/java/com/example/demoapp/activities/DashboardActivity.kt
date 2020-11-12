package com.example.demoapp.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.fragments.FavouritesFragment
import com.example.demoapp.fragments.NewsFragment
import com.example.demoapp.fragments.ProfileFragment
import com.example.demoapp.utils.addFragment
import com.example.demoapp.utils.replaceFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


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
        addFragment(NewsFragment(), R.id.fragment_container, supportFragmentManager)
        addBottomNavigationBar()
    }

    /**
     * Method to add bottom navigation view to the activity
     */
    private fun addBottomNavigationBar() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navlistener = BottomNavigationView.OnNavigationItemSelectedListener {
            lateinit var fragment: Fragment
            when (it.itemId) {
                R.id.page_news -> fragment = NewsFragment()
                R.id.page_favourites -> fragment = FavouritesFragment()
                R.id.page_profile -> fragment = ProfileFragment()
            }
            replaceFragment(fragment, R.id.fragment_container, supportFragmentManager)
            return@OnNavigationItemSelectedListener true
        }
        bottomNav.setOnNavigationItemSelectedListener(navlistener)
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