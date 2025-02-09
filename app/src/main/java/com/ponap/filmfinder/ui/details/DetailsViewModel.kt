package com.ponap.filmfinder.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ponap.filmfinder.data.MoviesRepository
import com.ponap.filmfinder.di.IoDispatcher
import com.ponap.filmfinder.di.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MoviesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    val selectedMovieLiveData = repository.selectedMovieLiveData

    private val _loadingUiState: MutableLiveData<LoadingUiState> = MutableLiveData(LoadingUiState())
    val loadingUiState: LiveData<LoadingUiState>
        get() = _loadingUiState

    init {
        fetchMovieDetails()
    }

    private fun fetchMovieDetails() {
        val movie = selectedMovieLiveData.value
        if (movie?.additionalDetails != null) {
            // we already have the details :)
            return
        }
        _loadingUiState.value = LoadingUiState(isLoading = true)

        viewModelScope.launch(ioDispatcher) {
            val imdbId = movie?.imdbId
            imdbId?.let {
                val ret = repository.fetchMovieDetails(imdbId)

                withContext(mainDispatcher) {
                    if (ret.isSuccess) {
                        // remove loader:
                        _loadingUiState.value = LoadingUiState()
                        // add the info to the movie and update the repository:
                        movie.additionalDetails = ret.getOrNull()
                        repository.setSelectedMovie(movie)
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

    data class LoadingUiState(
        val isLoading: Boolean = false,
        val error: String? = null
    )
}