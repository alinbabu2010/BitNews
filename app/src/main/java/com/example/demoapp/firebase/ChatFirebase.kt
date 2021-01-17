package com.example.demoapp.firebase

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.demoapp.models.ChatMessage
import com.example.demoapp.ui.activities.dashboard.ChatActivity.Companion.user
import com.example.demoapp.utils.Constants.Companion.MESSAGES
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * Singleton class to handle some chat firebase operations
 */
object ChatFirebase {

        private const val TAG = "ChatFirebase"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
        private val firebaseDatabaseReference = FirebaseDatabase.getInstance().reference

        /**
         * Function to store image im firebase storage
         * @param uri Uri of the image to be stored
         * @param message Message to be stored
         * @param activity An activity instance of current fragment
         */
        fun storeImageMessage(uri: Uri?, message: String,activity: FragmentActivity?) {
            val tempMessage = ChatMessage(
                text = message,
                name = user?.username.toString(),
                photoUrl = user?.userImageUrl.toString(),
                imageUrl = LOADING_IMAGE_URL
            )
            firebaseDatabaseReference.child(MESSAGES).push().setValue(tempMessage) { databaseError, databaseReference ->
                    if (databaseError == null) {
                        val key = databaseReference.key.toString()
                        val storageReference = FirebaseStorage.getInstance()
                            .getReference(user?.id.toString())
                            .child(key)
                            .child(uri?.lastPathSegment.toString())
                        activity?.let { activity ->
                            uri?.let { uri ->
                                storageReference.putFile(uri).addOnCompleteListener(activity) { task ->
                                    if (task.isSuccessful) {
                                        task.result.metadata?.reference?.downloadUrl?.addOnCompleteListener(activity) { task1 ->
                                            if (task.isSuccessful) {
                                                val chatMessage = ChatMessage(key, message, user?.username.toString(), user?.userImageUrl.toString(), task1.result.toString())
                                                firebaseDatabaseReference.child(MESSAGES).child(key).setValue(chatMessage)
                                            }
                                        }
                                    } else {
                                        Log.w(TAG, "Image upload task was not successful.", task.exception)
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Unable to write message to database", databaseError.toException())
                    }
            }
        }

        /**
         * Function to send message to user and store it in database
         * @param message Message to be stored
         * @param receiverId  User id of the user to which message is send
         */
        fun sendMessage(message: String, receiverId: String?) {
            val chatMessage = ChatMessage(
                text = message,
                name = user?.username.toString(),
                photoUrl = user?.userImageUrl.toString(),
                imageUrl = null
            )
            firebaseDatabaseReference.child(MESSAGES).child(getChatPersonsId(receiverId)).push()
                .setValue(chatMessage) { databaseError, databaseReference ->
                    if (databaseError == null) {
                        val key = databaseReference.key
                        databaseReference.child("id").setValue(key)
                    }
                }
        }

        /**
         * Function generate id for the user chat
         * @param receiverId  User id of the user to which message is send
         * @return Generated id for the chat
         */
        fun getChatPersonsId(receiverId: String?): String {
            val userId = user?.id.toString()
            val comparisonInt = userId.compareTo(receiverId.toString())
            return if( comparisonInt < 0) {
                "$userId-${receiverId.toString()}"
            } else {
                "${receiverId.toString()}-$userId"
            }
        }

}