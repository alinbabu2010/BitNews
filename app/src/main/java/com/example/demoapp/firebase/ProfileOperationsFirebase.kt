package com.example.demoapp.firebase

import android.net.Uri
import com.example.demoapp.fragments.dashboard.ProfileFragment.Companion.firebaseResponseMessage
import com.example.demoapp.models.Users
import com.example.demoapp.utils.Const.Companion.EMAIL_STRING
import com.example.demoapp.utils.Const.Companion.IMAGE_URL
import com.example.demoapp.utils.Const.Companion.NAME_STRING
import com.example.demoapp.utils.Const.Companion.PROFILE_IMAGE_DELETE
import com.example.demoapp.utils.Const.Companion.PROFILE_IMAGE_UPDATE
import com.example.demoapp.utils.Const.Companion.USERNAME_STRING
import com.example.demoapp.utils.Const.Companion.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class ProfileOperationsFirebase {

    companion object {

        private val mStorageRef = FirebaseStorage.getInstance().reference

        /**
         * Method to get user details from firebase.
         */
        fun getDataFromFirebase(data: (Map<String, String>) -> Unit) {
            val getCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
            val user = FirebaseDatabase.getInstance().getReference(USERS)
            val getUser = user.child(getCurrentUser.toString())
            getUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val profileInfo = mutableMapOf<String, String>()
                        profileInfo[NAME_STRING] = dataSnapshot.child(NAME_STRING).value as String
                        profileInfo[USERNAME_STRING] = dataSnapshot.child(USERNAME_STRING).value as String
                        profileInfo[EMAIL_STRING] = dataSnapshot.child(EMAIL_STRING).value as String
                        if (dataSnapshot.child(IMAGE_URL).value == null) {
                            profileInfo[IMAGE_URL] = "NONE"
                        } else {
                            profileInfo[IMAGE_URL] = dataSnapshot.child(IMAGE_URL).value as String
                        }
                        firebaseResponseMessage = null
                        data(profileInfo)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    firebaseResponseMessage = error.message
                }
            })

        }

        fun uploadImageToFirebase(
            file: Uri?,
            userData: Map<String, String>,
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
                                val user = Users(
                                    userData[USERNAME_STRING],
                                    userData[NAME_STRING],
                                    userData[EMAIL_STRING],
                                    uri.toString()
                                )
                                FirebaseDatabase.getInstance().getReference(USERS)
                                    .child(getCurrentUser).setValue(user)
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

        fun removeUserImage(){
            val getCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
            val storageRef = mStorageRef.child(getCurrentUser.toString()).child("images/$getCurrentUser.jpg")
            storageRef.delete().addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference(USERS).child(getCurrentUser.toString()).child("userImageUrl").removeValue()
                    .addOnSuccessListener {
                        firebaseResponseMessage = PROFILE_IMAGE_DELETE
                    }
            }

        }
    }
}