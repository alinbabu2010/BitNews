package com.example.demoapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.models.users

/**
 * A simple [Fragment] subclass for showing logged in user profile
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_profile, container, false)

        // Get the user details from shared preferences
        val sharedPreferences = context?.getSharedPreferences("app-userInfo", Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString("username", null).toString()
        val user = users.find{ it.username.contentEquals(username)}

        // Display the values get from shared preference to TextView
        inflatedView.findViewById<TextView>(R.id.username_display).text = user?.username
        inflatedView.findViewById<TextView>(R.id.name_display).text = user?.name
        inflatedView.findViewById<TextView>(R.id.email_display).text = user?.email

        return inflatedView
    }

}