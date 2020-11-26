package com.example.demoapp.utils

import com.example.demoapp.models.APIError

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

/**
 * Utility class for parsing error response
 */
class  ErrorUtils {

    fun parseError(response: Response<*>): APIError? {
        val converter: Converter<ResponseBody, APIError> = Services().getRetrofit("https://jsonplaceholder.typicode.com/")
            .responseBodyConverter(APIError::class.java, arrayOfNulls<Annotation>(0))
        val error: APIError?
        error = try {
            response.errorBody()?.let { converter.convert(it) }
        } catch (e: IOException) {
            return APIError()
        }
        return error
    }
}