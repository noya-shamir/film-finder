package com.ponap.filmfinder.model

data class Media(
    val title: String,
    val year: String,
    val imdbId: String,
    val type: String,
    val poster: String,
    var additionalDetails: MediaDetails? = null,
    var isFavorite: Boolean = false
) {
    companion object {
        fun fromApiSearchResponse(response: MediaBasicInfo): Media {
            return Media(
                title = response.title,
                year = response.year,
                imdbId = response.imdbId,
                type = response.type,
                poster = response.poster
            )
        }
    }
}
