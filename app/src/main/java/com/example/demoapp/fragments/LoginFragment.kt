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
@Suppress("DEPRECATION")
class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_login, container, false)

        val forgotTextView = inflatedView.findViewById<TextView>(R.id.forgot_password)
        clickableText(forgotTextView)

        return inflatedView
    }

    /**
     * Method to set the forgotTextView as clickable span
     */
    private fun clickableText(forgotTextView: TextView?) {

        val spannableString = SpannableString(forgotTextView?.text.toString())
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                forgotPassword()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        if (forgotTextView != null) {
            forgotTextView.text?.length?.let {
                spannableString.setSpan(
                    clickableSpan1, 0,
                    it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        forgotTextView?.text = spannableString
        forgotTextView?.movementMethod = LinkMovementMethod.getInstance()

    }

    /**
     * Method to replace the login fragment to forgot password fragment on clickable span
     */
    private fun forgotPassword() {
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragment_container, ForgotPasswordFragment())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

    // Data class for users
    data class Users(val username:String,val name:String,val email:String)

    private val users:ArrayList<Users> = arrayListOf(
        Users("alex","Alex John","alexjohn485@gmail.com"),
        Users("bob","Bob Thomas","bobt@outlook.com"),
        Users("alice1452","Alice Sanda","alics8752@gmail.com"),
        Users("Kevin","Kevin Dapper","kevind@rediffmail.com")
    )
}