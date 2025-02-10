package com.ponap.filmfinder.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ponap.filmfinder.data.MediaRepository
import com.ponap.filmfinder.di.IoDispatcher
import com.ponap.filmfinder.di.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MediaRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    val selectedMediaLiveData = repository.selectedMediaLiveData

    private val _loadingUiState: MutableLiveData<LoadingUiState> = MutableLiveData(LoadingUiState())
    val loadingUiState: LiveData<LoadingUiState>
        get() = _loadingUiState

    init {
        fetchMediaDetails()
    }

    private fun fetchMediaDetails() {
        val media = selectedMediaLiveData.value
        if (media?.additionalDetails != null) {
            // we already have the details :)
            return
        }
        _loadingUiState.value = LoadingUiState(isLoading = true)

        viewModelScope.launch(ioDispatcher) {
            val imdbId = media?.imdbId
            imdbId?.let {
                val ret = repository.fetchMediaDetails(imdbId)

                withContext(mainDispatcher) {
                    if (ret.isSuccess) {
                        // remove loader:
                        _loadingUiState.value = LoadingUiState()
                        // add the info to the media and update the repository:
                        media.additionalDetails = ret.getOrNull()
                        repository.setSelectedMedia(media)
                    } else {
                        // remove loader and inform the ui of error, if any:
                        _loadingUiState.value = LoadingUiState(
                            error = ret.exceptionOrNull()?.message
                        )
                    }

                }
            }
        }
    }

    fun toggleMediaIsFavorite() {
        val media = selectedMediaLiveData.value
        media?.let {
            media.isFavorite = !media.isFavorite
            if (media.isFavorite){
                repository.addToFavorites(media.imdbId)
            } else {
                repository.removeFromFavorites(media.imdbId)
            }
            repository.setSelectedMedia(media)
        }
    }

    data class LoadingUiState(
        val isLoading: Boolean = false,
        val error: String? = null
    )
}