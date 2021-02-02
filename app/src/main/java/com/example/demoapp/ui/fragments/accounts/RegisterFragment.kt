package com.example.demoapp.ui.fragments.accounts

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.demoapp.R
import com.example.demoapp.databinding.FragmentRegisterBinding
import com.example.demoapp.models.Users
import com.example.demoapp.ui.activities.dashboard.DashboardActivity
import com.example.demoapp.utils.Constants
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.example.demoapp.utils.Utils.Companion.isNetworkConnected
import com.example.demoapp.utils.Utils.Companion.replaceFragment
import com.example.demoapp.viewmodels.AccountsViewModel

/**
 * A simple [Fragment] subclass to register new users
 */
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private var viewModel: AccountsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.title = getString(R.string.register_string)
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerProgressBar.visibility = View.INVISIBLE
        binding.registerButton.setOnClickListener {
            binding.registerProgressBar.visibility = View.VISIBLE
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if(isNetworkConnected(context)) {
                if (validateForm()) registerUser()
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Toast.makeText(context, Constants.NO_INTERNET,Toast.LENGTH_SHORT).show()
            }

        }
        val loginTextView: TextView = view.findViewById(R.id.login_redirect)
        redirectToLogin(loginTextView)
    }

    /**
     * Method to validate form submitted
     * @return Return true if form is valid else false
     */
    private fun validateForm(): Boolean {
        val username = binding.usernameInputSignUp.text.toString()
        val name = binding.nameInputSignUp.text.toString()
        val email = binding.emailInputSignUp.text.toString()
        val password = binding.passwordInputSignUp.text.toString()
        val confirmPassword = binding.confirmPasswordInputSignUp.text.toString()
        var isFormValid = true

        if (username.isEmpty()) {
            binding.registerProgressBar.visibility = View.INVISIBLE
            binding.usernameInputSignUp.error = getString(R.string.field_empty_text)
            binding.usernameInputSignUp.isFocusable = true
            isFormValid = false
        }
        if (name.isEmpty()) {
            binding.registerProgressBar.visibility = View.INVISIBLE
            binding.nameInputSignUp.error = getString(R.string.field_empty_text)
            binding.nameInputSignUp.isFocusable = true
            isFormValid = false
        }
        if (email.isEmpty()) {
            binding.registerProgressBar.visibility = View.INVISIBLE
            binding.emailInputSignUp.error = getString(R.string.field_empty_text)
            binding.emailInputSignUp.isFocusable = true
            isFormValid = false
        }
        if (password.isEmpty()) {
            binding.registerProgressBar.visibility = View.INVISIBLE
            binding.passwordInputSignUp.error = getString(R.string.field_empty_text)
            binding.passwordInputSignUp.isFocusable = true
            isFormValid = false
        }
        if (confirmPassword.isEmpty()) {
            binding.registerProgressBar.visibility = View.INVISIBLE
            binding.confirmPasswordInputSignUp.error = getString(R.string.field_empty_text)
            binding.confirmPasswordInputSignUp.isFocusable = true
            isFormValid = false
        }
        if (password != confirmPassword) {
            binding.registerProgressBar.visibility = View.INVISIBLE
            Toast.makeText(context, R.string.password_matching_error, Toast.LENGTH_SHORT).show()
            isFormValid = false
        }

        return isFormValid
    }

    /**
     * Method to make clickable text view and redirect to login on click
     * @param loginTextView A instance of login [TextView]
     */
    private fun redirectToLogin(loginTextView: TextView) {
        val spannableTextView = SpannableString(loginTextView.text.toString())
        val clickableSpanTextView: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                replaceFragment(
                    LoginFragment(),
                    R.id.fragment_container,
                    fragmentManager
                )
            }
        }
        val start = spannableTextView.length - 4
        val end = spannableTextView.length
        spannableTextView.setSpan(
            clickableSpanTextView,
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        loginTextView.text = spannableTextView
        loginTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Method to create a news user
     */
    private fun registerUser() {
        val username = binding.usernameInputSignUp.text.toString()
        val name = binding.nameInputSignUp.text.toString()
        val email = binding.emailInputSignUp.text.toString()
        val password = binding.passwordInputSignUp.text.toString()
        val user = Users("",username, name, email,null)
        viewModel = activity?.let { ViewModelProviders.of(it).get(AccountsViewModel::class.java) }
        viewModel?.createUser(email, password, user)
        viewModel?.operationExecuted?.observe(viewLifecycleOwner, {
            if (it) {
                binding.registerProgressBar.visibility = View.INVISIBLE
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                startActivity(Intent(context, DashboardActivity::class.java))
                activity?.finish()
            } else {
                binding.registerProgressBar.visibility = View.INVISIBLE
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (firebaseError != null) {
                    Toast.makeText(context, firebaseError, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}