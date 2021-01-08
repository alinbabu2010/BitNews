package com.example.demoapp.repository

import com.example.demoapp.firebase.ProfileFirebase.Companion.getDataFromFirebase
import com.example.demoapp.models.Users
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
     * @param userName String instance to hold username
     * @param password String instance to hold user password
     * @param userRepository A object of repository class [UserRepository]
     * @param isSuccess A boolean callback function notify action is success or not
     */
    fun sigInUser(
        userName: String,
        password: String,
        userRepository: UserRepository,
        isSuccess: (Boolean) -> Unit
    ) {
        getAuthInstance.signInWithEmailAndPassword(userName, password).addOnCompleteListener {
            if (it.isSuccessful) {
                getDataFromFirebase { user ->
                    CoroutineScope(Dispatchers.IO).launch {
                        userRepository.insertUser(user)
                    }
                }
                isSuccess(it.isSuccessful)
            } else {
                isSuccess(false)
            }
        }
    }

    /**
     * Method to register a user to firebase
     * @param email A string value instance to hold user email address
     * @param password String instance to hold user password
     * @param user An object of [Users] holding user info
     * @param isSuccess A boolean callback function notify action is success or not
     */
    fun createUser(email: String, password: String, user: Users,isSuccess: (Boolean) -> Unit) {
        getAuthInstance.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.id = getAuthInstance.currentUser?.uid.toString()
                    updateUser(user) {
                        isSuccess(it)
                    }
                } else {
                    firebaseError = task.exception?.message
                    isSuccess(false)
                }
            }
    }

    /**
     * Method to reset user password
     * @param email A string value instance to hold user email address
     * @return Returns a boolean denoting the operation is success or not
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
     * @param user An object of [Users] holding user info
     * @param isSuccess A boolean callback function notify action is success or not
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