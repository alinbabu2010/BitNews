package com.example.demoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.models.Users
import com.example.demoapp.models.users
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple [Fragment] subclass for forgot password fragment
 */

class ForgotPasswordFragment : Fragment() {

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        inflatedView.findViewById<Button>(R.id.reset_button).setOnClickListener {
            val email = inflatedView.findViewById<TextInputEditText>(R.id.email_input).text.toString()

            // Checking email is valid or not
            if (email.matches(emailPattern.toRegex())){
                val validUser = users.isValid { it.email?.contentEquals(email) }
                if (validUser == null) {
                    Toast.makeText(context, "Email not associated with any account", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Password reset link send to email", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }

            } else {
                Toast.makeText(context, "Invalid email address entered", Toast.LENGTH_SHORT).show()
            }
        }
        return inflatedView
    }

}

/**
 * Extension function for ArrayList<Users> to check the email is with any user or not
 */
fun ArrayList<Users>.isValid(predicate: (Users) -> Boolean?): Users? {
    for (element in this) if (predicate(element)!!) {
        return element
    }
    return null
}