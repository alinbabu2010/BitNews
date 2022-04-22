package com.example.demoapp.repository

import com.example.demoapp.firebase.ChatFirebase.getChatPersonsId
import com.example.demoapp.firebase.ProfileFirebase.getDataFromFirebase
import com.example.demoapp.models.ChatMessage
import com.example.demoapp.models.Users
import com.example.demoapp.utils.Constants.Companion.MESSAGES
import com.example.demoapp.utils.Constants.Companion.USERS
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
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
                    isSuccess(it.isSuccessful)
                }
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
    fun createUser(email: String, password: String, user: Users, isSuccess: (Boolean) -> Unit) {
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
     * @param isSuccess A boolean callback function notify action is success or not
     */
    fun resetPassword(email: String, isSuccess: (Boolean) -> Unit) {
        firebaseError = null
        val validUser = getAuthInstance.sendPasswordResetEmail(email)
        validUser.addOnCompleteListener { task ->
            if (task.isSuccessful) isSuccess(task.isSuccessful)
            else {
                firebaseError = task.exception?.message
                isSuccess(false)
            }
        }
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

    /**
     * Method to get users list from firebase realtime database
     * @param options A callback to called function providing an instance of [FirebaseRecyclerOptions]
     */
    fun getUserList(options: (FirebaseRecyclerOptions<Users>) -> Unit) {
        val mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        val parser: SnapshotParser<Users> = SnapshotParser<Users> { dataSnapshot ->
            val users: Users? = dataSnapshot.getValue(Users::class.java)
            users?.id = dataSnapshot.key.toString()
            users as Users
        }
        val messagesRef = mFirebaseDatabaseReference.child(USERS)
        options(FirebaseRecyclerOptions.Builder<Users>().setQuery(messagesRef, parser).build())
    }

    /**
     * Method to get the user chat
     * @param receiverId  User id of the user to which message is send
     * @param options A callback to called function providing an instance of [FirebaseRecyclerOptions]
     */
    fun getUserChat(receiverId: String?, options: (FirebaseRecyclerOptions<ChatMessage>) -> Unit) {
        val mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(MESSAGES)
        val parser: SnapshotParser<ChatMessage> = SnapshotParser<ChatMessage> { dataSnapshot ->
            val friendlyMessage: ChatMessage? = dataSnapshot.getValue(ChatMessage::class.java)
            friendlyMessage?.id = dataSnapshot.key
            friendlyMessage as ChatMessage
        }
        val messagesRef = mFirebaseDatabaseReference.child(getChatPersonsId(receiverId))
        options(
            FirebaseRecyclerOptions.Builder<ChatMessage>().setQuery(messagesRef, parser).build()
        )
    }
}