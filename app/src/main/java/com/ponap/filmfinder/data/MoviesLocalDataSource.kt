package com.ponap.filmfinder.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesLocalDataSource @Inject constructor() {
    // a cache for favorite items, will still need to add persistence
    private val favorites: MutableSet<String> = mutableSetOf()

    fun addToFavorites(imdbId: String) {
        favorites.add(imdbId)
    }

    fun removeFromFavorites(imdbId: String) {
        favorites.remove(imdbId)
    }

    fun isFavorite(imdbId: String) = favorites.contains(imdbId)

}