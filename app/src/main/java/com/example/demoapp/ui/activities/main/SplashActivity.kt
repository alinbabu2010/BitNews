package com.example.demoapp.ui.activities.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.ui.activities.dashboard.DashboardActivity
import com.example.demoapp.utils.Constants.Companion.FAVOURITES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Splash screen  activity of demo app
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        with(FirebaseDatabase.getInstance()){
            setPersistenceEnabled(true)
            getReference(FAVOURITES).keepSynced(true)
        }

        // Check a user is logged in or not
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }, 2000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, DashboardActivity::class.java))
                this.finish()
            }, 2000)
        }

    }
}