package com.example.demoapp.fragments.accounts

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.utils.Const.Companion.EMAIL_PATTERN
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass for forgot password fragment
 */
class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = Resources.getSystem().getString(R.string.reset_password_string)

        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        val resetProgressBar : ProgressBar = inflatedView.findViewById(R.id.resetProgressBar)

        resetProgressBar.visibility = View.INVISIBLE


        inflatedView.findViewById<Button>(R.id.reset_button).setOnClickListener {
            val email = inflatedView.findViewById<TextInputEditText>(R.id.email_input).text.toString()
            resetProgressBar.visibility = View.VISIBLE
            resetPassword(email,resetProgressBar)

        }
        return inflatedView
    }

    /**
     * Method to check email and send the password reset link
     */
    private fun resetPassword(email: String, resetProgressBar: ProgressBar) {
        if (email.matches(EMAIL_PATTERN.toRegex())) {
            val validUser = FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            validUser.addOnCompleteListener {
                if(it.isSuccessful) {
                    resetProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, Resources.getSystem().getString(R.string.password_reset), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                else {
                    resetProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            resetProgressBar.visibility = View.INVISIBLE
            Toast.makeText(context, Resources.getSystem().getString(R.string.invalid_message), Toast.LENGTH_SHORT).show()
        }
    }
}

