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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.activities.DashboardActivity
import com.example.demoapp.utils.replaceFragment
import com.example.demoapp.utils.sharedPreferenceVariable
import com.example.demoapp.utils.startNewActivity
import com.example.demoapp.utils.users
import com.google.android.material.textfield.TextInputEditText


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

        inflatedView.findViewById<Button>(R.id.login_button).setOnClickListener {
            val userName = inflatedView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val password = inflatedView.findViewById<TextInputEditText>(R.id.password_input).text.toString()
            loginUser(userName,password)
        }
        return inflatedView
    }

    /**
     * Method to set the forgotTextView as clickable span
     */
    private fun clickableText(forgotTextView: TextView?) {

        val spannableTextView = SpannableString(forgotTextView?.text.toString())
        val clickableSpanTextView: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                navigateToForgotPassword()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        if (forgotTextView != null) {
            forgotTextView.text?.length?.let {
                spannableTextView.setSpan(
                    clickableSpanTextView, 0,
                    it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        forgotTextView?.text = spannableTextView
        forgotTextView?.movementMethod = LinkMovementMethod.getInstance()

    }

    /**
     * Method to replace the login fragment to forgot password fragment on clickable span
     */
    private fun navigateToForgotPassword() {
        fragmentManager?.let { replaceFragment(ForgotPasswordFragment(),R.id.fragment_container, it) }
    }

    /**
     * Method to check user provided login credentials and move to dashboard if it is true
     */
    private fun loginUser(userName: String, password: String) {
        var user = false
        var name: String? = null
        var email:String? = null
        users.forEach {
            if(it.username.contentEquals(userName) and it.password.contentEquals(password)){
                user = true
                name = it.name
                email = it.email
            }
        }

        if (user){
            val editor = (context?.let { sharedPreferenceVariable(it) })?.edit()
            editor?.putString("username",userName)
            editor?.putString("name",name)
            editor?.putString("email",email)
            editor?.apply()
            context?.let { startNewActivity(it,DashboardActivity()) }
            activity?.finish()

        } else {
            Toast.makeText(context,"Incorrect credentials entered",Toast.LENGTH_LONG).show()
        }



    }
}