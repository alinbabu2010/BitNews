package com.example.demoapp.ui.activities.dashboard

import android.os.Bundle
import android.transition.ChangeImageTransform
import android.transition.Slide
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R
import com.example.demoapp.models.Users
import com.example.demoapp.ui.fragments.chat.ChatUsersFragment
import com.example.demoapp.utils.Utils.Companion.addFragment
import com.example.demoapp.utils.Utils.Companion.showAlert
import com.example.demoapp.viewmodels.AccountsViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * An activity used to chat with users
 */
class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS)
            sharedElementEnterTransition = ChangeImageTransform()
            sharedElementExitTransition  = ChangeImageTransform()
            exitTransition = Slide(Gravity.END)
        }
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addFragment(ChatUsersFragment(), R.id.chat_layout, supportFragmentManager)
        val accountsViewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)
        accountsViewModel.getUserInfo(FirebaseAuth.getInstance().currentUser?.uid.toString())
        accountsViewModel.userData.observe(this) {
            user = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_option -> {
                val accountsViewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)
                showAlert(this, this, accountsViewModel)
                true
            }
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
                true
            }
            else -> false
        }
    }

    companion object {
        var user : Users? = null
    }
}