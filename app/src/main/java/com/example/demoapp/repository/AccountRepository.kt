package com.example.demoapp.repository

import com.example.demoapp.firebase.ProfileFirebase.Companion.getDataFromFirebase
import com.example.demoapp.models.Users
import com.example.demoapp.utils.Const.Companion.EMAIL_STRING
import com.example.demoapp.utils.Const.Companion.IMAGE_URL
import com.example.demoapp.utils.Const.Companion.NAME_STRING
import com.example.demoapp.utils.Const.Companion.USERNAME_STRING
import com.example.demoapp.utils.Const.Companion.USERS
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Repository class to perform account related firebase operations
 */
class AccountRepository {

    private val getAuthInstance = FirebaseAuth.getInstance()
    private var isSuccess = false
    var user: Users? = null

    /**
     * Method to sign in a user
     */
    fun sigInUser(
        userName: String,
        password: String,
        userRepository: UserRepository,
        isSuccess: (Boolean) -> Unit
    ) {
        getAuthInstance.signInWithEmailAndPassword(userName, password).addOnCompleteListener {
            if (it.isSuccessful) {
                getDataFromFirebase { data ->
                    user = Users(
                        getAuthInstance.currentUser?.uid.toString(),
                        data[USERNAME_STRING],
                        data[NAME_STRING],
                        data[EMAIL_STRING],
                        data[IMAGE_URL]
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        user?.let { it1 ->
                            userRepository.insertUser(
                                it1
                            )
                        }
                    }
                    isSuccess(it.isSuccessful)
                }
            } else {
                isSuccess(false)
            }
        }
    }

    /**
     * Method to register a user to firebase
     */
    fun createUser(email: String, password: String, user: Users): Boolean {
        getAuthInstance.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateUser(user) {
                        isSuccess = it
                    }
                } else {
                    firebaseError = task.exception?.message
                    isSuccess = false
                }
            }
        return isSuccess
    }

    /**
     * Method to reset user password
     */
    fun resetPassword(email: String): Boolean {
        firebaseError = null
        val validUser = getAuthInstance.sendPasswordResetEmail(email)
        validUser.addOnCompleteListener { task ->
            if (task.isSuccessful) isSuccess = task.isSuccessful
            else {
                firebaseError = task.exception?.message
                isSuccess = false
            }
        }
        return isSuccess
    }

    /**
     * Method to update a user info in firebase
     */
    fun updateUser(user: Users, isSuccess: (Boolean) -> Unit) {
        getAuthInstance.currentUser?.uid?.let { it1 ->
            FirebaseDatabase.getInstance().getReference(USERS)
                .child(it1).setValue(user).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isSuccess(task.isSuccessful)
                    } else {
                        firebaseError = task.exception?.message
                        isSuccess(false)
                    }
                }
        }
    }
}