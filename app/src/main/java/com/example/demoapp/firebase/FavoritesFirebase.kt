package com.example.demoapp.firebase

import com.example.demoapp.models.Articles
import com.example.demoapp.utils.Constants.Companion.FAVOURITES
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * Singleton class to handle some firebase operations
 */
object FavoritesFirebase {

    /**
     * Method to store favourite articles of user
     * @param favouriteArticles A mutable set of [Articles]
     * @param isSuccess A callback function to notify action is success or not
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
     * @param favouriteArticles A callback function to pass [MutableSet] of [Articles]
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