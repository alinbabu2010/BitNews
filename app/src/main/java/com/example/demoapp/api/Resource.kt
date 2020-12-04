package com.example.demoapp.api

import com.example.demoapp.models.APIError
import com.example.demoapp.utils.Const.Companion.NETWORK_FAILURE
import com.example.demoapp.utils.Const.Companion.NOT_FOUND
import com.example.demoapp.utils.Const.Companion.SERVER_ERROR
import java.io.IOException

/**
 * Resource class for handling request response states
 */
data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val exception: Throwable?
) {
    companion object {

        fun <T> success(data: T): Resource<T> = Resource<T>(Status.SUCCESS,data,null,null)

        fun <T> error(apiError: APIError?): Resource<T> {
            val errorMessage = when (apiError?.status()){
                404 -> "${apiError.status()} - $NOT_FOUND"
                500 -> "${apiError.status()} - $SERVER_ERROR"
                else -> "${apiError?.status()} - ${apiError?.message()}"
            }
            return Resource<T>(Status.ERROR,null,errorMessage,null)
        }

        fun <T> failure(exception: Throwable): Resource<T> {
            val message : String = if( exception is IOException )  NETWORK_FAILURE
            else exception.message.toString()
            return Resource<T>(Status.ERROR, null, message, exception)
        }

        fun <T> loading(data: T?): Resource<T> = Resource<T>(Status.LOADING,data,null,null)
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }
}