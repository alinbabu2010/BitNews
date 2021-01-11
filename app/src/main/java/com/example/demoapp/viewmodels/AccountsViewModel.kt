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

    var userData = MutableLiveData<Users?>()
    private val userRepository: UserRepository
    private val accountRepository = AccountRepository()

    init {
        val usersDAO = UserDatabase.getDatabase(application).usersDAO()
        userRepository = UserRepository(usersDAO)
    }

    /**
     * Method to call [AccountRepository.sigInUser]
     * @param userName String value instance for username
     * @param password String value instance for user password
     */
    fun signInUser(userName: String, password: String) {
        accountRepository.sigInUser(userName, password, userRepository) {
            operationExecuted.postValue(it)
        }
    }

    /**
     * Method to call [AccountRepository.createUser]
     * @param email String value instance for user email address
     * @param password String value instance for user password
     * @param user An object of class [Users]
     */
    fun createUser(email: String, password: String, user: Users) {
        accountRepository.createUser(email, password, user){
            user.id = FirebaseAuth.getInstance().currentUser?.uid.toString()
            user.userImageUrl = "NONE"
            CoroutineScope(Dispatchers.IO).launch {
                userRepository.insertUser(user)
            }
            operationExecuted.postValue(it)
        }
    }

    /**
     * Method to call [AccountRepository.resetPassword]
     * @param email String value instance for user email address
     */
    fun resetPassword(email: String) {
        operationExecuted.value = accountRepository.resetPassword(email)
    }

    /**
     * Method to call [UserRepository.getCurrentUserInfo]
     * @param uid String value instance for user id
     */
    fun getUserInfo(uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var user : Users? = null
            userRepository.getCurrentUserInfo(uid) {
                user = it
                userData.postValue(it)
            }
            user?.let { userRepository.insertUser(it) }
        }
    }

    /**
     * Method to call [UserRepository.deleteUser]
     * @param uid String value instance for user id
     */
    fun removeUserInfo(uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.deleteUser(uid)
        }
    }

    /**
     * Method to call [UserRepository.updateUser]
     * @param user An object of class [Users]
     * @param isSuccess Boolean callback function for user info update success or not
     */
    fun updateUserInfoOnDatabase(user: Users, isSuccess: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.updateUser(user)
        }
        isSuccess(true)
    }

    /**
     * Method to call [AccountRepository.updateUser]
     * @param user An object of class [Users]
     * @return Boolean for user info update success or not
     */
    fun updateUserInfoOnFirebase(user: Users): Boolean {
        var isSuccess = true
        accountRepository.updateUser(user) {
            isSuccess = it
        }
        return isSuccess
    }
}