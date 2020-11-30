package com.example.demoapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.fragments.RegisterFragment
import com.example.demoapp.utils.addFragment

/**
 * Main activity class of demo app
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adding login fragment to activity layout
        addFragment(RegisterFragment(), R.id.fragment_container, supportFragmentManager)

        /**
         * Enable back button in actionbar only  if there are entries in the back stack
         */
        supportFragmentManager.addOnBackStackChangedListener {
            val callback: Boolean = supportFragmentManager.backStackEntryCount > 0
            supportActionBar?.setDisplayHomeAsUpEnabled(callback)
        }
    }

    /**
     * This method is called when the back button in actionbar  is pressed.
     */
    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }
}