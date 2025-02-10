package com.ponap.filmfinder.model

data class MediaDetails(
    val genre: String,
    val director: String,
    val writer: String,
    val actors: String,
    val plot: String,
    val imdbRating: String,
) {
    companion object {
        fun fromApiDetailsResponse(response: MediaDetailsResponse): MediaDetails {
            return MediaDetails(
                genre = response.genre ?: "",
                director = response.director ?: "",
                writer = response.writer ?: "",
                actors = response.actors ?: "",
                plot = response.plot ?: "",
                imdbRating = response.imdbRating ?: "",
            )
        }
    }
}