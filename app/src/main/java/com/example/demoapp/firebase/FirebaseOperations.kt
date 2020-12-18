package com.example.demoapp.firebase

import com.example.demoapp.models.Articles
import com.example.demoapp.utils.Const.Companion.FAVOURITES
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * Singleton class to handle some firebase operations
 */
class FirebaseOperations {

    companion object {

        /**
         * Method to store favourite articles of user
         */
        fun storeDataOnFirebase(
            favouriteArticles: MutableSet<Articles>?,
            isSuccess: (Boolean) -> Unit
        ) {
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                FirebaseDatabase.getInstance().getReference(FAVOURITES).child(it)
                    .setValue(favouriteArticles?.toList()).addOnSuccessListener {
                        isSuccess(true)
                    }
            }
        }


        /**
         * Method to retrieve the favourite articles of particular user
         */
        fun retrieveDataFromFirebase(favouriteArticles: (MutableSet<Articles>?) -> Unit) {
            firebaseError = null
            val getCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().getReference(FAVOURITES).child(getCurrentUser.toString())
                .orderByKey().addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val t: GenericTypeIndicator<MutableList<Articles>> =
                            object : GenericTypeIndicator<MutableList<Articles>>() {}
                        val articles = dataSnapshot.getValue(t)?.toMutableSet()
                        favouriteArticles(articles)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        firebaseError = error.message
                    }
                })
        }
    }
}