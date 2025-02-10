package com.ponap.filmfinder.model

import com.google.gson.annotations.SerializedName

data class MediaSearchResponse(
    @SerializedName("Search") val mediaResults: List<MediaBasicInfo>? = null,
    val totalResults: String? = null,
    @SerializedName("Response") val apiResponse: String,
    @SerializedName("Error") val error: String? = null
)

data class MediaBasicInfo(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("imdbID") val imdbId: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val poster: String
)
