package com.example.demoapp.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demoapp.R
import com.example.demoapp.activities.ArticleActivity
import com.example.demoapp.activities.MainActivity
import com.example.demoapp.models.News.Articles
import com.google.firebase.auth.FirebaseAuth

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


/*
 * Function to show alert dialog box after button click.
 */
fun showAlert(context: Context, activity: Activity) {
    val builder = AlertDialog.Builder(context, R.style.DialogBoxTheme)
    builder.setTitle(LOGOUT_STRING)
    builder.setMessage(ALERT_DIALOG_QUESTION)
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    builder.setPositiveButton(YES) { _, _ ->
        FirebaseAuth.getInstance().signOut()
        activity.startActivity(Intent(context, MainActivity::class.java))
        activity.finish()
    }
    builder.setNegativeButton(NO) { _: DialogInterface, _: Int -> }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

/**
 * Function to start news detail activity
 */
fun openArticle(context: Context, article: Articles?) {
    val intent = Intent(context, ArticleActivity::class.java)
    intent.putExtra("article", article)
    ContextCompat.startActivity(context, intent, Bundle.EMPTY)
}

/**
 * Function to share news to others
 */
fun shareNews(context: Context, url: String?) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    ContextCompat.startActivity(context, shareIntent, Bundle.EMPTY)
}