package com.example.demoapp.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.fragments.ProfileFragment
import com.example.demoapp.utils.addFragment


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
        addFragment(ProfileFragment(), R.id.dashboard_container, supportFragmentManager)
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
    private fun showAlert(){
        val builder = AlertDialog.Builder(this,R.style.DialogBoxTheme)
        builder.setTitle("Logout")
        builder.setMessage("Do you really want to logout?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){ _, _ ->
            startActivity(Intent(this, MainActivity::class.java))
            getSharedPreferences("app-userInfo", Context.MODE_PRIVATE).edit().clear().apply()
            finish()
        }
        builder.setNegativeButton("No"){ _: DialogInterface, _: Int -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}