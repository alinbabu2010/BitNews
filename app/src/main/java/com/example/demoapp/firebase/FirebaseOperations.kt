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
        fun storeDataOnFirebase(favouriteArticles: MutableSet<Articles>?, isSuccess : (Boolean) -> Unit){
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                val favourites = FirebaseDatabase.getInstance().getReference(FAVOURITES).child(it).setValue(favouriteArticles?.toList())
                favourites.addOnSuccessListener {
                    isSuccess(true)
                }
            }
        }

        /**
         * Method to retrieve the favourite articles of particular user
         */
        fun retrieveDataFromFirebase(favouriteArticles: (MutableSet<Articles>?) -> Unit) {
            val getCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
            val user = FirebaseDatabase.getInstance().getReference(FAVOURITES)
            val favourites = user.orderByKey().equalTo(getCurrentUser.toString())
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