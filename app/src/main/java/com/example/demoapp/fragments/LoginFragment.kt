package com.example.demoapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.activities.DashboardActivity
import com.google.android.material.textfield.TextInputEditText
import kotlin.properties.Delegates


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
    data class Users(val username:String, val password: String,val name:String?, val email: String?)

    // Array list of users
    private val users:ArrayList<Users> = arrayListOf(
        Users("alex","alex123","Alex John","alexjohn485@gmail.com"),
        Users("bob","bob321","Bob Thomas","bobt@outlook.com"),
        Users("alice1452","alice1452","Alice Sanda","alics8752@gmail.com"),
        Users("Kevin","k987321","Kevin Dapper","kevind@rediffmail.com")
    )

    /**
     * Method to check user provided login credentials and move to dashboard if it is true
     */
    private fun loginUser(userName: String, password: String) {
        var user by Delegates.notNull<Boolean>()
        users.forEach {
            if(it.username.contentEquals(userName) and it.password.contentEquals(password)){
                    user = true
            }
        }

        if (user){
            val intent = Intent(context,DashboardActivity::class.java)
            startActivity(intent)
            activity?.finish()

        } else {
            Toast.makeText(context,"Incorrect credentials entered",Toast.LENGTH_LONG).show()
        }



    }
}