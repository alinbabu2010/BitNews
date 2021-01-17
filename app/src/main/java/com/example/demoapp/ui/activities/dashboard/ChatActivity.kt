package com.example.demoapp.ui.activities.dashboard

import android.os.Bundle
import android.transition.ChangeImageTransform
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
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
        }
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addFragment(ChatUsersFragment(), R.id.chat_layout, supportFragmentManager)
        val accountsViewModel = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
        accountsViewModel.getUserInfo(FirebaseAuth.getInstance().currentUser?.uid.toString())
        accountsViewModel.userData.observe(this, {
            user = it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logout_option) {
            val accountsViewModel = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
            showAlert(this, this, accountsViewModel)
            true
        } else {
            false
        }
    }

    companion object {
        var user : Users? = null
    }
}