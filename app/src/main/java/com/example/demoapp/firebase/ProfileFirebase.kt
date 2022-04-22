package com.example.demoapp.firebase

import android.net.Uri
import com.example.demoapp.models.Users
import com.example.demoapp.ui.fragments.dashboard.ProfileFragment.Companion.firebaseResponseMessage
import com.example.demoapp.utils.Constants.Companion.EMAIL_STRING
import com.example.demoapp.utils.Constants.Companion.IMAGE_URL
import com.example.demoapp.utils.Constants.Companion.NAME_STRING
import com.example.demoapp.utils.Constants.Companion.PROFILE_IMAGE_UPDATE
import com.example.demoapp.utils.Constants.Companion.USERNAME_STRING
import com.example.demoapp.utils.Constants.Companion.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


object ProfileFirebase {

    private val mStorageRef = FirebaseStorage.getInstance().reference

    /**
     * Method to get user details from firebase.
     * @param data A callback function to pass [Users] object.
     */
    fun getDataFromFirebase(data: (Users) -> Unit) {
        firebaseResponseMessage = null
        val getCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().getReference(USERS)
        val getUser = userRef.child(getCurrentUser.toString())
        getUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val imageUrl = if (dataSnapshot.child(IMAGE_URL).value == null) {
                        "NONE"
                    } else {
                        dataSnapshot.child(IMAGE_URL).value as String
                    }
                    val user = Users(
                        getCurrentUser.toString(),
                        dataSnapshot.child(USERNAME_STRING).value as String,
                        dataSnapshot.child(NAME_STRING).value as String,
                        dataSnapshot.child(EMAIL_STRING).value as String,
                        imageUrl
                    )
                    firebaseResponseMessage = null
                    data(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                firebaseResponseMessage = error.message
            }
        })

    }

    /**
     * Method to upload image to firebase
     * @param file An instance of [Uri] that contains Uri of image to upload
     * @param message A callback function that passes a response message
     */
    fun uploadImageToFirebase(
        file: Uri?,
        message: (String) -> Unit
    ) {

        val getCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef =
            getCurrentUser?.let { mStorageRef.child(it).child("images/$getCurrentUser.jpg") }
        file?.let { it ->
            storageRef?.putFile(it)
                ?.addOnCompleteListener { taskSnapshot -> // Get a URL to the uploaded content
                    if (taskSnapshot.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            FirebaseDatabase.getInstance().getReference(USERS)
                                .child(getCurrentUser).child("userImageUrl")
                                .setValue(uri.toString())
                                .addOnSuccessListener {
                                    message(PROFILE_IMAGE_UPDATE)
                                }
                        }
                    } else {
                        message(taskSnapshot.exception?.message.toString())
                    }
                }
        }
    }

    /**
     * Method to remove user image from firebase
     * @param isSuccess A callback to calling function to know operation has succeeded or not
     */
    fun removeUserImage(isSuccess: (Boolean) -> Unit) {
        val getCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef =
            mStorageRef.child(getCurrentUser.toString()).child("images/$getCurrentUser.jpg")
        storageRef.delete().addOnCompleteListener {
            isSuccess(it.isSuccessful)
        }
    }
}