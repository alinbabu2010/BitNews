package com.example.demoapp.ui.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.ui.activities.dashboard.DashboardActivity
import com.example.demoapp.utils.Constants.Companion.FAVOURITES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Launcher activity of demo app
 */
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(FirebaseDatabase.getInstance()) {
            setPersistenceEnabled(true)
            getReference(FAVOURITES).keepSynced(true)
        }

        // Check a user is logged in or not
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
            this.finish()
        }

    }
}