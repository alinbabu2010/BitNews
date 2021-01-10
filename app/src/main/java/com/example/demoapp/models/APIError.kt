package com.example.demoapp.models

/**
 * Model class for API error message model
 */
class APIError {
    private val statusCode = 0
    private val message: String? = null

    fun status(): Int {
        return statusCode
    }

    fun message(): String? {
        return message
    }
}
