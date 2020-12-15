package com.example.demoapp.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demoapp.R
import com.example.demoapp.activities.ArticleActivity
import com.example.demoapp.activities.ImageDetailActivity
import com.example.demoapp.activities.MainActivity
import com.example.demoapp.models.Articles
import com.example.demoapp.utils.Const.Companion.ARTICLE
import com.example.demoapp.utils.Const.Companion.NO_INTERNET
import com.example.demoapp.utils.Const.Companion.SHARE_TYPE
import com.google.firebase.auth.FirebaseAuth

/**
 * Utility class for holding utility functions
 */
class Utils {

    companion object {

        var firebaseError: String? = null

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
        fun replaceFragment(fragment: Fragment, id: Int, fragmentManager: FragmentManager?) {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(id, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }


        /*
         * Function to show alert dialog box after button click.
         */
        fun showAlert(context: Context, activity: Activity) {
            val builder = AlertDialog.Builder(context, R.style.DialogBoxTheme)
            builder.setTitle(R.string.logout_string)
            builder.setMessage(R.string.alert_dialog_question)
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton(R.string.yes_string) { _, _ ->
                FirebaseAuth.getInstance().signOut()
                activity.startActivity(Intent(context, MainActivity::class.java))
                activity.finish()
            }
            builder.setNegativeButton(R.string.no_string) { _: DialogInterface, _: Int -> }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        /**
         * Function to start news detail activity
         */
        fun openArticle(context: Context, article: Articles?) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARTICLE, article)
            ContextCompat.startActivity(context, intent, Bundle.EMPTY)
        }

        /**
         * Function to share news to others
         */
        fun shareNews(context: Context, url: String?) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = SHARE_TYPE
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            ContextCompat.startActivity(context, shareIntent, Bundle.EMPTY)
        }

        /**
         * Method that check network connection before calling the request function
         */
        fun checkNetworkConnection(context: Context?, function: () -> Unit) {
            if (isNetworkConnected(context)) {
                function()
            } else {
                Toast.makeText(
                    context,
                    NO_INTERNET,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        /**
         * Method to check if network is connected or not
         */
        private fun isNetworkConnected(context: Context?): Boolean {

            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        /**
         * Function to start dummy activity
         */
        fun openDummyActivity(context: Context, article: Articles?) {
            val intent = Intent(context, ImageDetailActivity::class.java)
            intent.putExtra(ARTICLE, article)
            ContextCompat.startActivity(context, intent, Bundle.EMPTY)
        }

        /**
         * Method to show dialog about permission request
         */
        fun requestPermissionRationale(
            context: Context?,
            activity: Activity?,
            requestingType: String
        ) {
            val builder = context?.let { AlertDialog.Builder(it, R.style.DialogBoxTheme) }
            builder?.setTitle("Permission Needed")
            builder?.setMessage("Please enable $requestingType permission in app settings")
            builder?.setIcon(android.R.drawable.ic_dialog_alert)
            builder?.setPositiveButton("OK") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${activity?.packageName}")
                )
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity?.startActivity(intent)
            }
            val alertDialog: AlertDialog? = builder?.create()
            alertDialog?.setCancelable(false)
            alertDialog?.show()
        }
    }
}