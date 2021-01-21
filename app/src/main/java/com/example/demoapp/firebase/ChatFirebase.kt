package com.example.demoapp.firebase

import android.net.Uri
import android.util.Log
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
     * @param receiverId  User id of the user to which message is send
     */
    fun storeImageMessage(
        uri: Uri?,
        message: String,
        receiverId: String?
    ) {
        val tempMessage = ChatMessage(
            text = message,
            senderId = user?.id.toString(),
            imageUrl = LOADING_IMAGE_URL
        )
        firebaseDatabaseReference.child(MESSAGES).child(getChatPersonsId(receiverId)).push()
            .setValue(tempMessage) { databaseError, databaseReference ->
                if (databaseError == null) {
                    val key = databaseReference.key.toString()
                    val storageReference = FirebaseStorage.getInstance()
                        .getReference(user?.id.toString())
                        .child("messages/${uri?.lastPathSegment.toString()}")
                    uri?.let { uri ->
                        storageReference.putFile(uri).addOnSuccessListener {
                            storageReference.downloadUrl.addOnSuccessListener { task1 ->
                                val chatMessage = ChatMessage(
                                    key,
                                    user?.id.toString(),
                                    message,
                                    task1.toString()
                                )
                                firebaseDatabaseReference.child(MESSAGES)
                                    .child(getChatPersonsId(receiverId)).child(key)
                                    .setValue(chatMessage)
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
            senderId  = user?.id.toString(),
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
        return if (comparisonInt < 0) {
            "$userId-${receiverId.toString()}"
        } else {
            "${receiverId.toString()}-$userId"
        }
    }

}