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
import com.example.demoapp.databinding.FragmentLoginBinding
import com.example.demoapp.ui.activities.dashboard.DashboardActivity
import com.example.demoapp.utils.Constants
import com.example.demoapp.utils.Utils
import com.example.demoapp.utils.Utils.Companion.replaceFragment
import com.example.demoapp.viewmodels.AccountsViewModel


/**
 * A simple [Fragment] subclass for login
 */
class LoginFragment : Fragment() {

    private var viewModel: AccountsViewModel? = null
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.actionBar?.setDisplayShowTitleEnabled(true)
        activity?.title = getString(R.string.login_string)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val forgotTextView = binding.forgotPassword
        clickableText(
            forgotTextView,
            forgotTextView.length() - 13,
            forgotTextView.length() - 9,
            ForgotPasswordFragment()
        )
        val registerTextView = binding.registerRedirect
        clickableText(
            registerTextView,
            registerTextView.length() - 4,
            registerTextView.length(),
            null
        )
        binding.progressBar.visibility = View.INVISIBLE
        binding.loginButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            val userName = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (userName.isBlank() && password.isBlank()) {
                Toast.makeText(context, R.string.field_empty_text, Toast.LENGTH_SHORT).show()
            } else {
                if(Utils.isNetworkConnected(context)) {
                    binding.progressBar.visibility = View.VISIBLE
                    loginUser(userName, password)
                } else {
                    Toast.makeText(context, Constants.NO_INTERNET, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.INVISIBLE
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    /**
     * Method to set the [TextView] as clickable span and replace the fragment on click.
     * @param view An instance to access [TextView] in [LoginFragment]
     * @param start Start position of text to be clickable
     * @param end End position of text to be clickable
     * @param fragment An instance of [Fragment] class
     */
    private fun clickableText(view: TextView?, start: Int, end: Int, fragment: Fragment?) {

        val spannableTextView = SpannableString(view?.text.toString())
        val clickableSpanTextView: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (fragment != null) {
                    replaceFragment(
                        fragment,
                        R.id.fragment_container,
                        fragmentManager
                    )
                }
                else {
                    fragmentManager?.popBackStack()
                }
            }
        }
        spannableTextView.setSpan(
            clickableSpanTextView,
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view?.text = spannableTextView
        view?.movementMethod = LinkMovementMethod.getInstance()

    }

    /**
     * Method to check user provided login credentials and move to [DashboardActivity] if it is true
     * @param userName String value instance for username
     * @param password String value instance for user password
     */
    private fun loginUser(userName: String, password: String) {
        viewModel = activity?.let { ViewModelProviders.of(it).get(AccountsViewModel::class.java) }
        viewModel?.signInUser(userName, password)
        viewModel?.operationExecuted?.observe(viewLifecycleOwner) {
            if (it != null && it == true) {
                startActivity(Intent(context, DashboardActivity::class.java))
                activity?.finish()
            }
            if (it != null && it == false) {
                binding.progressBar.visibility = View.INVISIBLE
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Toast.makeText(context, R.string.wrong_credentials_text, Toast.LENGTH_SHORT).show()
                viewModel?.operationExecuted?.value = null
            }
        }
    }
}