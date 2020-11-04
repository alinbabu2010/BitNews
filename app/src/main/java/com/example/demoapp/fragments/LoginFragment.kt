package com.example.demoapp.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.demoapp.R


/**
 * A simple [Fragment] subclass for login
 */
class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_login, container, false)

        val forgotTextView = inflatedView.findViewById<TextView>(R.id.forgot_password)
        val ss = SpannableString(forgotTextView.text.toString())
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                forgotPassword()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan1, 0, forgotTextView.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        forgotTextView.text = ss
        forgotTextView.movementMethod = LinkMovementMethod.getInstance()
        return inflatedView
    }

    private fun forgotPassword() {
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragment_container, ForgotPasswordFragment())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

}