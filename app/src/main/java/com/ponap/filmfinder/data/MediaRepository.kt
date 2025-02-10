package com.ponap.filmfinder.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ponap.filmfinder.model.Media
import com.ponap.filmfinder.model.MediaDetails
import com.ponap.filmfinder.model.MediaSearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val localDataSource: MediaLocalDataSource,
    private val remoteDataSource: MediaRemoteDataSource
) {

    private val _selectedMediaLiveData: MutableLiveData<Media?> = MutableLiveData(null)
    val selectedMediaLiveData: LiveData<Media?>
        get() = _selectedMediaLiveData


    suspend fun searchMediaByPage(text: String, page: Int): Result<MediaSearchResponse> {
        return remoteDataSource.searchMediaByPage(text, page)
    }

    suspend fun fetchMediaDetails(imdbId: String): Result<MediaDetails> {
        return remoteDataSource.fetchMediaDetails(imdbId)
    }

    fun setSelectedMedia(media: Media?) {
        _selectedMediaLiveData.value = media
    }

    fun addToFavorites(imdbId: String) {
        localDataSource.addToFavorites(imdbId)
    }

    fun removeFromFavorites(imdbId: String) {
        localDataSource.removeFromFavorites(imdbId)
    }

    fun isFavorite(imdbId: String) = localDataSource.isFavorite(imdbId)

}