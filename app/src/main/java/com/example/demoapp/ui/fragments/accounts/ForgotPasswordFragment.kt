package com.example.demoapp.ui.fragments.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R
import com.example.demoapp.utils.Constants.Companion.EMAIL_PATTERN
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.example.demoapp.viewmodels.AccountsViewModel
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple [Fragment] subclass for forgot password fragment
 */
class ForgotPasswordFragment : Fragment() {

    private var viewModel: AccountsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = getString(R.string.reset_password_string)

        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        val resetProgressBar: ProgressBar = inflatedView.findViewById(R.id.resetProgressBar)

        resetProgressBar.visibility = View.INVISIBLE


        inflatedView.findViewById<Button>(R.id.reset_button).setOnClickListener {
            val email =
                inflatedView.findViewById<TextInputEditText>(R.id.email_input).text.toString()
            resetProgressBar.visibility = View.VISIBLE
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            resetPassword(email, resetProgressBar)

        }
        return inflatedView
    }

    /**
     * Method to check email and send the password reset link
     * @param email String value instance for user email address
     * @param resetProgressBar A variable to access fragment [ProgressBar]
     */
    private fun resetPassword(email: String, resetProgressBar: ProgressBar) {
        if (email.matches(EMAIL_PATTERN.toRegex())) {
            viewModel = activity?.let { ViewModelProvider(it).get(AccountsViewModel::class.java) }
            viewModel?.resetPassword(email)
            viewModel?.operationExecuted?.observe(viewLifecycleOwner) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (it) {
                    resetProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, R.string.password_reset, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    resetProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, firebaseError, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            resetProgressBar.visibility = View.INVISIBLE
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            Toast.makeText(context, R.string.invalid_message, Toast.LENGTH_SHORT).show()
        }
    }
}

