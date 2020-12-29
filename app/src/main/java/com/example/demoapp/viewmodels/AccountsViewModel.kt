package com.example.demoapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.demoapp.database.UserDatabase
import com.example.demoapp.models.Users
import com.example.demoapp.repository.AccountRepository
import com.example.demoapp.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for handling accounts related operations
 */
class AccountsViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for notifying whether a firebase operation executed successfully or not
    val operationExecuted: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    var userData = MutableLiveData<Users>()
    private val userRepository: UserRepository
    private val accountRepository = AccountRepository()

    init {
        val usersDAO = UserDatabase.getDatabase(application).usersDAO()
        userRepository = UserRepository(usersDAO)
    }

    /**
     * Method to call [AccountRepository.sigInUser]
     */
    fun signInUser(userName: String, password: String) {
        accountRepository.sigInUser(userName, password, userRepository) {
            operationExecuted.postValue(it)
        }
    }

    /**
     * Method to call [AccountRepository.createUser]
     */
    fun createUser(email: String, password: String, user: Users) {
        operationExecuted.value = accountRepository.createUser(email, password, user)
        user.id = FirebaseAuth.getInstance().currentUser?.uid.toString()
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.insertUser(user)
        }
    }

    /**
     * Method to call [AccountRepository.resetPassword]
     */
    fun resetPassword(email: String) {
        operationExecuted.value = accountRepository.resetPassword(email)
    }

    /**
     * Method to call [UserRepository.getCurrentUserInfo]
     */
    fun getUserInfo(uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = userRepository.getCurrentUserInfo(uid)
            userData.postValue(data)
        }
    }

    /**
     * Method to call [UserRepository.deleteUser]
     */
    fun removeUserInfo(uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.deleteUser(uid)
        }
    }

    /**
     * Method to call [UserRepository.updateUser] and [AccountRepository.updateUser]
     */
    fun updateUserInfo(user: Users, isSuccess: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.updateUser(user)
        }
        accountRepository.updateUser(user) {
            isSuccess(it)
        }
    }
}