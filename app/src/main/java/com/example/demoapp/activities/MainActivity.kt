package com.example.demoapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.fragments.LoginFragment
import com.example.demoapp.utils.addFragment

/**
 * Main activity class of demo app
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adding login fragment to activity layout
        addFragment(LoginFragment(), R.id.fragment_container, supportFragmentManager)
    }
}