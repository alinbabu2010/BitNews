package com.example.demoapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.fragments.ProfileFragment

/**
 * Activity class for dashboard of logged user
 */

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.dashboard_container, ProfileFragment())
        fragmentTransaction.commit()
        setContentView(R.layout.activity_dashboard)
    }
}