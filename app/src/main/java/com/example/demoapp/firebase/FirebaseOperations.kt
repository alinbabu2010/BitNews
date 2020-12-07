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

        private val firebaseReference =
            FirebaseDatabase.getInstance().getReference(FAVOURITES)

        /**
         * Method to get the current user
         */
        fun getCurrentUser(): String? {
            return FirebaseAuth.getInstance().currentUser?.uid
        }

        /**
         * Method to store favourite articles of user
         */
        fun storeDataOnFirebase(favouriteArticles: MutableSet<Articles>?): Boolean {
            var isSuccess = false
            val currentUser = getCurrentUser()
            currentUser?.let {
                val favourites = firebaseReference.child(it).setValue(favouriteArticles?.toList())
                favourites.addOnSuccessListener {
                    isSuccess = true
                }
            }
            return isSuccess
        }

        /**
         * Method to retrieve the favourite articles of particular user
         */
        fun retrieveDataFromFirebase(favouriteArticles: (MutableSet<Articles>?) -> Unit) {
            val currentUser = getCurrentUser()
            val favourites = firebaseReference.orderByKey().equalTo(currentUser)
            favourites.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val t: GenericTypeIndicator<MutableList<Articles>> = object : GenericTypeIndicator<MutableList<Articles>>() {}
                    var articles : MutableSet<Articles>? = mutableSetOf()
                    for (snapshot in dataSnapshot.children){
                        articles = snapshot.getValue(t)?.toMutableSet()
                    }
                    firebaseError = null
                    favouriteArticles(articles)
                }

                override fun onCancelled(error: DatabaseError) {
                    firebaseError = error.message
                }
            })
        }
    }
}