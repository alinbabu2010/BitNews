package com.example.demoapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.utils.sharedPreferenceVariable
import com.example.demoapp.utils.startNewActivity

/**
 * Splash screen  activity of demo app
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Get the values in shared preferences
        val sharedPreferences = sharedPreferenceVariable(this)
        val username = sharedPreferences?.getString("username", null)
        val password = sharedPreferences?.getString("password", null)

        // Check the values in shared preference is null or not
        if (username.isNullOrBlank() && password.isNullOrBlank()) {
            Handler(Looper.getMainLooper()).postDelayed({
                startNewActivity(this, MainActivity())
                this.finish()
            }, 2000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startNewActivity(this, DashboardActivity())
                this.finish()
            }, 2000)
        }


    }
}