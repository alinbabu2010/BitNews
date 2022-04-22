package com.example.demoapp.utils

import com.example.demoapp.api.RetrofitManager
import com.example.demoapp.models.APIError

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

/**
 * Utility class for parsing error response
 */
class ErrorUtils {

    /**
     * Method to parse error response
     * @param response An instance of class [Response]
     * @return An object of class [APIError]
     */
    fun parseError(response: Response<*>): APIError? {
        val converter: Converter<ResponseBody, APIError> = RetrofitManager.getRetrofit
            .responseBodyConverter(APIError::class.java, arrayOfNulls<Annotation>(0))
        val error: APIError? = try {
            response.errorBody()?.let { converter.convert(it) }
        } catch (e: IOException) {
            return APIError()
        }
        return error
    }
}