package com.example.demoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.firebase.FirebaseOperations
import com.example.demoapp.firebase.FirebaseOperations.Companion.getAuthInstance
import com.example.demoapp.models.Users
import com.example.demoapp.utils.Const
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.google.firebase.database.FirebaseDatabase

/**
 * ViewModel for handling accounts related operations
 */
class AccountsViewModel : ViewModel() {

    // LiveData for notifying whether a firebase operation executed successfully or not
    val operationExecuted: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    /**
     * Method to sign in a user
     */
    fun sigInUser(userName: String, password: String) {
        getAuthInstance.signInWithEmailAndPassword(userName, password).addOnCompleteListener {
            if (it.isSuccessful) {
                operationExecuted.value = it.isSuccessful
            } else {
                operationExecuted.value = false
            }
        }
    }

    /**
     * Method to register a user to firebase
     */
    fun createUser(email: String, password: String, user: Users) {
        getAuthInstance.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseOperations.getCurrentUser?.let { it1 ->
                        FirebaseDatabase.getInstance().getReference(Const.USERS)
                            .child(it1).setValue(user).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    operationExecuted.value = it.isSuccessful
                                } else {
                                    firebaseError = task.exception?.message
                                    operationExecuted.value = false
                                }
                            }
                    }
                } else {
                    firebaseError = it.exception?.message
                    operationExecuted.value = false
                }
            }
    }

    /**
     * Method to reset user password
     */
    fun resetPassword(email: String) {
        firebaseError = null
        val validUser = getAuthInstance.sendPasswordResetEmail(email)
        validUser.addOnCompleteListener { task ->
            if (task.isSuccessful) operationExecuted.value = task.isSuccessful
            else {
                firebaseError = task.exception?.message
                operationExecuted.value = false
            }
        }
    }

}