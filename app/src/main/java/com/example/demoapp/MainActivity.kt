package com.example.demoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Main activity class of demo app
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Adding login fragment to activity layout
        val loginFragment = LoginFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container,loginFragment)
        fragmentTransaction.commit()
        setContentView(R.layout.activity_main)
    }
}