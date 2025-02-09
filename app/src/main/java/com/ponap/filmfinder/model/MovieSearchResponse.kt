package com.ponap.filmfinder.model

import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @SerializedName("Search") val movies: List<MovieBasicInfo>? = null,
    val totalResults: String? = null,
    @SerializedName("Response") val apiResponse: String,
    @SerializedName("Error") val error: String? = null
)

data class MovieBasicInfo(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("imdbID") val imdbId: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val poster: String
)
