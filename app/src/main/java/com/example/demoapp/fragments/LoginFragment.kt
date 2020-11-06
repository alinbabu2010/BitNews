package com.example.demoapp.fragments

import android.content.Context
import android.content.Intent
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
import com.example.demoapp.models.users
import com.example.demoapp.utils.replaceFragment
import com.google.android.material.textfield.TextInputEditText


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
        clickableText(forgotTextView)

        inflatedView.findViewById<Button>(R.id.login_button).setOnClickListener {
            val userName =
                inflatedView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val password =
                inflatedView.findViewById<TextInputEditText>(R.id.password_input).text.toString()
            loginUser(userName, password)
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
                replaceFragment(ForgotPasswordFragment(), R.id.fragment_container, parentFragmentManager)
            }
        }
        spannableTextView.setSpan(clickableSpanTextView, 28, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        forgotTextView?.text = spannableTextView
        forgotTextView?.movementMethod = LinkMovementMethod.getInstance()

    }

    /**
     * Method to check user provided login credentials and move to dashboard if it is true
     */
    private fun loginUser(userName: String, password: String) {
        val user = users.find { it.username.contentEquals(userName) and it.password.contentEquals(password) }
        if (user == null){
            Toast.makeText(context, "Incorrect credentials entered", Toast.LENGTH_LONG).show()
        }  else {
            val editor = context?.getSharedPreferences("app-userInfo", Context.MODE_PRIVATE)?.edit()
            editor?.putString("username", user.username)
            editor?.putString("name", user.name)
            editor?.putString("email", user.email)
            editor?.apply()
            startActivity(Intent(context, DashboardActivity::class.java))
            activity?.finish()

        }
    }
}