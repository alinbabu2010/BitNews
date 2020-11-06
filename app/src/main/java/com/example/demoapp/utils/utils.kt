package com.example.demoapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Function to add a new fragment to the layout
 */
fun addFragment(fragment: Fragment, id: Int, fragmentManager: FragmentManager) {
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.add(id, fragment)
    fragmentTransaction.commit()
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