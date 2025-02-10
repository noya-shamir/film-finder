package com.ponap.filmfinder.data

import com.ponap.filmfinder.model.MediaDetails
import com.ponap.filmfinder.model.MediaSearchResponse
import com.ponap.filmfinder.network.OmdbService
import com.ponap.filmfinder.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRemoteDataSource @Inject constructor(
    private val service: OmdbService
) {

    suspend fun searchMediaByPage(text: String, page: Int) = safeApiCall(
        call = { requestSearchMediaByPage(text, page) },
        errorMessage = "error in searchMediaByPage for $text"
    )

    suspend fun fetchMediaDetails(imdbId: String) = safeApiCall(
        call = { requestFetchMediaDetails(imdbId) },
        errorMessage = "error fetching media details"
    )


    private suspend fun requestSearchMediaByPage(
        text: String,
        page: Int
    ): Result<MediaSearchResponse> {
        val response = service.search(text, page)
        var errorMessage: String? = null
        if (response.isSuccessful) {
            if (response.body() != null) {
                return Result.success(response.body()!!)
            } else {
                errorMessage = response.body()?.error ?: "no list"
            }
        }
        return Result.failure(Exception(errorMessage ?: "response wasn't successful"))
    }

    private suspend fun requestFetchMediaDetails(imdbId: String): Result<MediaDetails> {
        val response = service.getMediaDetails(imdbId)
        var errorMessage: String? = null
        if (response.isSuccessful) {
            if (response.body()?.apiResponse?.lowercase() == "true") {
                val details = MediaDetails.fromApiDetailsResponse(response.body()!!)
                return Result.success(details)
            } else {
                errorMessage = response.body()?.error ?: "media details unexpected response"
            }
        }
        return Result.failure(Exception(errorMessage ?: "response wasn't successful"))
    }
}