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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.activities.DashboardActivity
import com.example.demoapp.models.Users
import com.example.demoapp.utils.replaceFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass to register new users
 */
class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerProgressBar.visibility = View.INVISIBLE
        register_button.setOnClickListener {
            registerProgressBar.visibility = View.VISIBLE
            registerUser()
        }
        val loginTextView :TextView = view.findViewById(R.id.login_redirect)
        redirectToLogin(loginTextView)
    }

    private fun redirectToLogin(loginTextView: TextView) {
        val spannableTextView = SpannableString(loginTextView.text.toString())
        val clickableSpanTextView: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                replaceFragment(
                    LoginFragment(),
                    R.id.fragment_container,
                    parentFragmentManager
                )
            }
        }
        spannableTextView.setSpan(clickableSpanTextView, 31, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginTextView.text = spannableTextView
        loginTextView.movementMethod = LinkMovementMethod.getInstance()

    }

    /**
     * Method to register a user to firebase
     */
    private fun registerUser() {
        val username = username_input_signUp.text.toString()
        val name = name_input_signUp.text.toString()
        val email = email_input_signUp.text.toString()
        val password = password_input_signUp.text.toString()
        val confirmPassword = confirmPassword_input_signUp.text.toString()

        if(password == confirmPassword) {
            val auth = FirebaseAuth.getInstance()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = Users(username,password,name,email)
                        FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(it1).setValue(user).addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        registerProgressBar.visibility = View.INVISIBLE
                                        startActivity(Intent(context, DashboardActivity::class.java))
                                        activity?.finish()
                                    }
                                    else {
                                        registerProgressBar.visibility = View.INVISIBLE
                                        Toast.makeText(context,task.exception?.message,Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    else {
                        registerProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            registerProgressBar.visibility = View.INVISIBLE
            Toast.makeText(context,"Password not matching",Toast.LENGTH_SHORT).show()
        }
    }


}