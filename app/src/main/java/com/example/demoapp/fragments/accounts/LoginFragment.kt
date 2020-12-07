package com.example.demoapp.fragments.accounts

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.activities.DashboardActivity
import com.example.demoapp.utils.Utils.Companion.checkNetworkConnection
import com.example.demoapp.utils.Utils.Companion.replaceFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass for login
 */
class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.actionBar?.setDisplayShowTitleEnabled(true)
        activity?.title = getString(R.string.login_string)

        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_login, container, false)

        val forgotTextView = inflatedView.findViewById<TextView>(R.id.forgot_password)
        clickableText(forgotTextView,forgotTextView.length()-13,forgotTextView.length()-9, ForgotPasswordFragment())

        val registerTextView = inflatedView.findViewById<TextView>(R.id.register_redirect)
        clickableText(registerTextView,registerTextView.length()-4,registerTextView.length(), RegisterFragment())

        val progressBar : ProgressBar = inflatedView.findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        inflatedView.findViewById<Button>(R.id.login_button).setOnClickListener {
            val userName =
                inflatedView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val password =
                inflatedView.findViewById<TextInputEditText>(R.id.password_input).text.toString()
            if (userName.isBlank() && password.isBlank()) {
                Toast.makeText(context, R.string.field_empty_text, Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.VISIBLE
                checkNetworkConnection(context) {
                   loginUser(userName, password,progressBar)
                }
            }
        }
        return inflatedView
    }

    /**
     * Method to set the [TextView] as clickable span and replace the fragment on click.
     */
    private fun clickableText(view: TextView?,start :Int,end:Int,fragment: Fragment) {

        val spannableTextView = SpannableString(view?.text.toString())
        val clickableSpanTextView: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                replaceFragment(
                    fragment,
                    R.id.fragment_container,
                    parentFragmentManager
                )
            }
        }
        spannableTextView.setSpan(clickableSpanTextView, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view?.text = spannableTextView
        view?.movementMethod = LinkMovementMethod.getInstance()

    }

    /**
     * Method to check user provided login credentials and move to [DashboardActivity] if it is true
     */
    private fun loginUser(userName: String, password: String, progressBar: ProgressBar) {
        val userAuth = FirebaseAuth.getInstance()
        userAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener {
            if(it.isSuccessful){
                startActivity(Intent(context, DashboardActivity::class.java))
                activity?.finish()
            } else {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(context,R.string.wrong_credentials_text, Toast.LENGTH_LONG).show()
            }
        }
    }
}