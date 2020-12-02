package com.example.demoapp.api

import androidx.lifecycle.MutableLiveData
import com.example.demoapp.models.APIError
import com.example.demoapp.utils.NOT_FOUND
import com.example.demoapp.utils.SERVER_ERROR

/**
 * Class to handle API responses
 */
class APIResponse<T> {

    /**
     * Method to handle on success response
     */
    fun success(response: T): MutableLiveData<T> {
        val apiData = MutableLiveData<T>()
        apiData.value = response
        return apiData
    }

    /**
     * Method to handle on error response
     */
    fun error(response: APIError?): String {
        return when (response?.status()){
            404 -> NOT_FOUND
            500 -> SERVER_ERROR
            else -> response?.message().toString()
        }
    }
}

