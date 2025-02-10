package com.ponap.filmfinder.network

import com.ponap.filmfinder.model.MediaDetailsResponse
import com.ponap.filmfinder.model.MediaSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://www.omdbapi.com/"

interface OmdbService {
    @GET("/")
    suspend fun search(
        @Query("s") searchText: String,
        @Query("page") page: Int?,
    ): Response<MediaSearchResponse>

    @GET("/")
    suspend fun getMediaDetails(
        @Query("i") mediaId: String,
    ): Response<MediaDetailsResponse>
}