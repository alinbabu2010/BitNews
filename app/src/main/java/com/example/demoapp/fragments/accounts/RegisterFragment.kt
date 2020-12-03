package com.example.demoapp.fragments.accounts

import android.content.Intent
import android.content.res.Resources
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
import com.example.demoapp.databinding.FragmentRegisterBinding
import com.example.demoapp.models.Users
import com.example.demoapp.utils.Utils.Companion.replaceFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * A simple [Fragment] subclass to register new users
 */
class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerProgressBar.visibility = View.INVISIBLE
        binding.registerButton.setOnClickListener {
            binding.registerProgressBar.visibility = View.VISIBLE
            registerUser()
        }
        val loginTextView :TextView = view.findViewById(R.id.login_redirect)
        redirectToLogin(loginTextView)
    }

    /**
     * Method to make clickable text view and redirect to login on click
     */
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
        val start = spannableTextView.length - 4
        val end = spannableTextView.length
        spannableTextView.setSpan(clickableSpanTextView, start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginTextView.text = spannableTextView
        loginTextView.movementMethod = LinkMovementMethod.getInstance()

    }

    /**
     * Method to register a user to firebase
     */
    private fun registerUser() {
        val username = binding.usernameInputSignUp.text.toString()
        val name = binding.nameInputSignUp.text.toString()
        val email = binding.emailInputSignUp.text.toString()
        val password = binding.passwordInputSignUp.text.toString()
        val confirmPassword = binding.confirmPasswordInputSignUp.text.toString()

        if(password == confirmPassword) {
            val auth = FirebaseAuth.getInstance()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = Users(username,name,email)
                        FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(it1).setValue(user).addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        binding.registerProgressBar.visibility = View.INVISIBLE
                                        startActivity(Intent(context, DashboardActivity::class.java))
                                        activity?.finish()
                                    }
                                    else {
                                        binding.registerProgressBar.visibility = View.INVISIBLE
                                        Toast.makeText(context,task.exception?.message,Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    else {
                        binding.registerProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            binding.registerProgressBar.visibility = View.INVISIBLE
            Toast.makeText(context, Resources.getSystem().getString(R.string.password_matching_error),Toast.LENGTH_SHORT).show()
        }
    }


}