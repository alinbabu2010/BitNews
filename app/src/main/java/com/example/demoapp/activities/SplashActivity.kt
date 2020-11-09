package com.example.demoapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R

/**
 * Splash screen  activity of demo app
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Get the values in shared preferences
        val sharedPreferences = getSharedPreferences("app-userInfo", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        // Check the values in shared preference is null or not
        if (username.isNullOrBlank() ) {
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