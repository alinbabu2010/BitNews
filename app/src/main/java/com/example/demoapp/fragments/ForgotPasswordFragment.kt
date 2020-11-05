package com.example.demoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.demoapp.R

/**
 * A simple [Fragment] subclass for forgot password fragment
 */
@Suppress("DEPRECATION")
class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        inflatedView.findViewById<Button>(R.id.back_button).setOnClickListener {
            fragmentManager?.popBackStack()
        }
        return inflatedView
    }

}