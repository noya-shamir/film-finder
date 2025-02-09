package com.ponap.filmfinder.network

import java.io.IOException

/**
 * inline function to wrap a suspending API [call] in try/catch.
 * If an exception is thrown, a [Result.failure] is returned, with IOException using the [errorMessage].
 */
suspend inline fun <T : Any> safeApiCall(
    call: suspend () -> Result<T>,
    errorMessage: String
): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        // An exception was thrown when calling the API so we're converting this to an IOException
        val message = e.message?.let { "$errorMessage: ${e.message}" } ?: errorMessage
        Result.failure(IOException(message, e))
    }
}