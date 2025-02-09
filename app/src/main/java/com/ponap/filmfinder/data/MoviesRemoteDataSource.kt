package com.ponap.filmfinder.data

import com.ponap.filmfinder.model.MovieDetails
import com.ponap.filmfinder.model.MovieSearchResponse
import com.ponap.filmfinder.network.OmdbService
import com.ponap.filmfinder.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRemoteDataSource @Inject constructor(
    private val service: OmdbService
) {

    suspend fun searchMoviesByPage(text: String, page: Int) = safeApiCall(
        call = { requestSearchMoviesByPage(text, page) },
        errorMessage = "error in searchMoviesByPage for $text"
    )

    suspend fun fetchMovieDetails(imdbId: String) = safeApiCall(
        call = { requestFetchMovieDetails(imdbId) },
        errorMessage = "error fetching Movie details"
    )


    private suspend fun requestSearchMoviesByPage(
        text: String,
        page: Int
    ): Result<MovieSearchResponse> {
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

    private suspend fun requestFetchMovieDetails(movieId: String): Result<MovieDetails> {
        val response = service.getMovieDetails(movieId)
        var errorMessage: String? = null
        if (response.isSuccessful) {
            if (response.body()?.apiResponse?.lowercase() == "true") {
                val details = MovieDetails.fromApiDetailsResponse(response.body()!!)
                return Result.success(details)
            } else {
                errorMessage = response.body()?.error ?: "movie details unexpected response"
            }
        }
        return Result.failure(Exception(errorMessage ?: "response wasn't successful"))
    }
}