package com.example.demoapp.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demoapp.R
import com.example.demoapp.models.Articles
import com.example.demoapp.ui.activities.dashboard.ArticleActivity
import com.example.demoapp.ui.activities.dashboard.ChatActivity
import com.example.demoapp.ui.activities.dashboard.FullscreenImageActivity
import com.example.demoapp.ui.activities.dashboard.ImageDetailActivity
import com.example.demoapp.ui.activities.main.MainActivity
import com.example.demoapp.utils.Constants.Companion.ARTICLE
import com.example.demoapp.utils.Constants.Companion.SHARE_TYPE
import com.example.demoapp.viewmodels.AccountsViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Utility class for holding utility functions
 */
class Utils {

    companion object {

        var firebaseError: String? = null

        /**
         * Function to add a new fragment to the layout
         * @param fragment An instance of [Fragment]
         * @param id Integer to provider container view id
         * @param fragmentManager An instance of [FragmentManager]
         */
        fun addFragment(fragment: Fragment, id: Int, fragmentManager: FragmentManager) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(id, fragment)
            fragmentTransaction.commit()
        }

        /**
         * Function to replace a fragment by another
         * @param fragment An instance of [Fragment]
         * @param id Integer to provider container view identity
         * @param fragmentManager An instance of [FragmentManager]
         */
        fun replaceFragment(fragment: Fragment, id: Int, fragmentManager: FragmentManager?) {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(id, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }


        /**
         * Function to show alert dialog box after button click
         * @param context  Access to the [Context] the calling function fragment is currently associated with.
         * @param activity Access to [Activity] referenced
         * @param accountsViewModel An instance of [AccountsViewModel]
         */
        fun showAlert(context: Context, activity: Activity, accountsViewModel: AccountsViewModel) {
            val builder = AlertDialog.Builder(context, R.style.DialogBoxTheme)
            builder.setTitle(R.string.logout_string)
            builder.setMessage(R.string.alert_dialog_question)
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton(R.string.yes_string) { _, _ ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseAuth.getInstance().signOut()
                accountsViewModel.removeUserInfo(uid.toString())
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
         * @param context  Access to the [Context] the calling function fragment is currently associated with.
         * @param article An object of class [Articles]
         */
        fun openArticle(context: Context, article: Articles?) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARTICLE, article)
            ContextCompat.startActivity(context, intent, Bundle.EMPTY)
        }

        /**
         * Function to share news to others
         * @param context  Access to the [Context] the calling function fragment is currently associated with.
         * @param url Url of news [Articles] to be shared
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
         * Method to check if network is connected or not
         * @param context  Access to the [Context] the calling function has access.
         */
        fun isNetworkConnected(context: Context?): Boolean {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        /**
         * Function to start dummy activity
         * @param context  Access to the [Context] the calling function fragment is currently associated with.
         * @param article An object of class [Articles]
         */
        fun openDummyActivity(context: Context, article: Articles?) {
            val intent = Intent(context, ImageDetailActivity::class.java)
            intent.putExtra(ARTICLE, article)
            ContextCompat.startActivity(context, intent, Bundle.EMPTY)
        }

        /**
         * Method to show dialog about permission request
         * @param context Access to the [Context] the calling function fragment is currently associated with.
         * @param activity Access to [Activity] referenced
         * @param requestingMessageId String resource id for requesting message
         */
        fun requestPermissionRationale(
            context: Context?,
            activity: Activity?,
            requestingMessageId: Int
        ) {
            val builder = context?.let { AlertDialog.Builder(it, R.style.DialogBoxTheme) }
            builder?.setTitle("Permission Needed")
            builder?.setMessage(requestingMessageId)
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

        /**
         * Function to start chat activity
         * @param context  Access to the [Context] the calling function activity
         */
        fun openChat(context: Context) {
            val intent = Intent(context, ChatActivity::class.java)
            ContextCompat.startActivity(context, intent, Bundle.EMPTY)
        }

        /**
         * Function to view image in full screen as new activity
         * @param imageUrl Url of the image to be loaded
         * @param messageImageView Image view of the image to be loaded
         * @param activity Activity instance of the calling fragment
         */
        fun loadPhoto(imageUrl: String, messageImageView: ImageView, activity: Activity?) {
            val intent = Intent(activity, FullscreenImageActivity::class.java)
            val transitionName = activity?.getString(R.string.image_message)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                messageImageView,
                transitionName
            )
            intent.putExtra("image", imageUrl)
            activity?.let { ContextCompat.startActivity(it, intent, options.toBundle()) }
        }
    }
}