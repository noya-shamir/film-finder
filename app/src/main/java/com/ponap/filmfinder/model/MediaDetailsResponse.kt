package com.ponap.filmfinder.model

import com.google.gson.annotations.SerializedName

// Note: we are only parsing the data used by the app
// the actual response may include other data, e.g. Rated, Released, Runtime, Language, Awards,
// Ratings (an array of objects with Source and Value), Metascore, imdbVotes, DVD, BocOffice,
// Production, Website, Country, totalSeasons
data class MediaDetailsResponse(
    @SerializedName("Title") val title: String? = null,
    @SerializedName("Year") val year: String? = null,
    @SerializedName("Genre") val genre: String?,
    @SerializedName("Director") val director: String?,
    @SerializedName("Writer") val writer: String?,
    @SerializedName("Actors") val actors: String?,
    @SerializedName("Plot") val plot: String?,
    @SerializedName("Poster") val poster: String?,
    val imdbRating: String?,
    @SerializedName("imdbID") val imdbId: String?,
    @SerializedName("Type") val type: String?,
    @SerializedName("Response") val apiResponse: String,
    @SerializedName("Error") val error: String?
)

