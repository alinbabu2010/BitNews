package com.example.demoapp.api

import com.example.demoapp.models.APIError

/**
 * Class to handle API responses
 */
sealed class APIResponse<out T : Any> {
    data class Success<out T : Any>(val data: T?) : APIResponse<T>()
    data class Error<out T : Any>(val error: APIError?) : APIResponse<T>()
}