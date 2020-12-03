package com.example.demoapp.api

import android.content.res.Resources
import com.example.demoapp.R
import com.example.demoapp.models.APIError
import java.io.IOException

/**
 * Resource class for handling request response states
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?,val exception: Throwable?) {
    companion object {

        fun <T> success(data: T): Resource<T> = Resource(Status.SUCCESS,data,null,null)

        fun <T> error(data: T?, apiError: APIError?): Resource<T> {
            val errorMessage = when (apiError?.status()){
                404 -> "${apiError.status()} - ${Resources.getSystem().getString(R.string.not_found_text)}"
                500 -> "${apiError.status()} - ${Resources.getSystem().getString(R.string.server_error_message)}"
                else -> "${apiError?.status()} - ${apiError?.message()}"
            }
            return Resource(Status.ERROR,data,errorMessage,null)
        }

        fun <T> failure(data: T?, exception: Throwable): Resource<T> {
            val message : String = if( exception is IOException ) {
                Resources.getSystem().getString(R.string.network_error_message)
            } else {
                exception.message.toString()
            }
            return Resource(Status.ERROR, data, message, exception)
        }

        fun <T> loading(data: T?): Resource<T> = Resource(Status.LOADING,data,null,null)
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }
}