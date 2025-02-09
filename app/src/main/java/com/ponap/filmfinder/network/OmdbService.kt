package com.ponap.filmfinder.network

import com.ponap.filmfinder.model.MovieDetailsResponse
import com.ponap.filmfinder.model.MovieSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val OMDB_API_KEY = "c7254a1"
const val BASE_URL = "https://www.omdbapi.com/"

interface OmdbService {
    @GET("/")
    suspend fun search(
        @Query("s") searchText: String,
        @Query("page") page: Int?,
    ): Response<MovieSearchResponse>

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") movieId: String,
    ): Response<MovieDetailsResponse>
}