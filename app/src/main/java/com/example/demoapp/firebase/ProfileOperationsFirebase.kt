package com.example.demoapp.firebase

import com.example.demoapp.firebase.FirebaseOperations.Companion.getCurrentUser
import com.example.demoapp.utils.Const
import com.example.demoapp.utils.Const.Companion.EMAIL_STRING
import com.example.demoapp.utils.Const.Companion.NAME_STRING
import com.example.demoapp.utils.Const.Companion.USERNAME_STRING
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileOperationsFirebase {

    companion object {

        /**
         * Method to get user details from firebase.
         */
        fun getDataFromFirebase(data: (Map<String,String>) -> Unit) {
            val user = FirebaseDatabase.getInstance().getReference(Const.USERS)
            val getUser = user.orderByKey().equalTo(getCurrentUser)
            getUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        val profileInfo = mutableMapOf<String,String>()
                        profileInfo[NAME_STRING] = getCurrentUser?.let { dataSnapshot.child(it).child(NAME_STRING).value } as String
                        profileInfo[USERNAME_STRING] = getCurrentUser.let { dataSnapshot.child(it).child(USERNAME_STRING).value } as String
                        profileInfo[EMAIL_STRING] = getCurrentUser.let { dataSnapshot.child(it).child(EMAIL_STRING).value } as String
                        firebaseError = null
                        data(profileInfo)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    firebaseError = error.message
                }
            })

        }
    }
}