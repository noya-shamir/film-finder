package com.ponap.filmfinder.model

data class Movie(
    val title: String,
    val year: String,
    val imdbId: String,
    val type: String,
    val poster: String,
    var additionalDetails: MovieDetails? = null
) {
    companion object {
        fun fromApiSearchResponse(response: MovieBasicInfo): Movie {
            return Movie(
                title = response.title,
                year = response.year,
                imdbId = response.imdbId,
                type = response.type,
                poster = response.poster
            )
        }
    }
}
