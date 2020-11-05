package com.example.demoapp.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


// Data class for users
data class Users(val username: String, val password: String, val name: String?, val email: String?)

// Array list of users
val users: ArrayList<Users> = arrayListOf(
    Users("alex", "alex123", "Alex John", "alexjohn485@gmail.com"),
    Users("bob", "bob321", "Bob Thomas", "bobt@outlook.com"),
    Users("alice1452", "alice1452", "Alice Sanda", "alics8752@gmail.com"),
    Users("Kevin", "k987321", "Kevin Dapper", "kevind@rediffmail.com")
)

/**
 * Function to add a new fragment to the layout
 */
fun addFragment(fragment: Fragment, id: Int, fragmentManager: FragmentManager) {
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.add(id, fragment)
    fragmentTransaction.commit()
}

/**
 * Function to return the shared preferences variable
 */
fun sharedPreferenceVariable(context: Context): SharedPreferences? {
    return context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
}

/**
 * Function to start a new activity
 */
fun startNewActivity(context: Context, activity: AppCompatActivity) {
    val intent = Intent(context, activity::class.java)
    context.startActivity(intent)
}

/**
 * Function to replace a fragment by another
 */
fun replaceFragment(fragment: Fragment, id: Int, fragmentManager: FragmentManager) {
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(id, fragment)
    fragmentTransaction.addToBackStack(null)
    fragmentTransaction.commit()
}