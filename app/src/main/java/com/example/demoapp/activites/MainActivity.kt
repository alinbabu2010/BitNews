package com.example.demoapp.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demoapp.fragments.LoginFragment
import com.example.demoapp.R

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