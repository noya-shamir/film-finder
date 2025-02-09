package com.ponap.filmfinder.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ponap.filmfinder.data.MoviesRepository
import com.ponap.filmfinder.model.Movie
import com.ponap.filmfinder.ui.search.adapter.SearchPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MoviesRepository
) : ViewModel() {

    /** live data of the selected movie, from the repository */
    val selectedMovieLiveData = repository.selectedMovieLiveData

    /** var to keep track of the user input, which we to pass to the paging data source */
    private var currentQueryText = ""

    /** paging data source, which we create when needed and also replace whenever invalidated */
    private var pagingSource: SearchPagingSource? = null
        get() {
            if (field == null || field?.invalid == true) {
                field = SearchPagingSource(repository = repository, query = currentQueryText)
            }
            return field!!
        }

    /** stream of PagingData for the search results adapter */
    val flow = Pager(
        PagingConfig(pageSize = 10)
    ) {
        pagingSource!!
    }.flow.cachedIn(viewModelScope)

    fun submitQuery(userQuery: String) {
        currentQueryText = userQuery.trim()
        pagingSource?.invalidate()
        repository.setSelectedMovie(null)
    }

    fun getCurrentQuery() = currentQueryText

    fun isNewQuery(newText: String?): Boolean = newText?.trim() != currentQueryText

    /**
     * checks if this movie is selected or un-selected, and informs the repository
     * @return true if the movie was selected, false otherwise
     * */
    fun setSelectedMovieAsNeeded(movie: Movie): Boolean {
        return if (isMovieAlreadySelected(movie.imdbId)) {
            // this is a case on un-selecting a movie:
            repository.setSelectedMovie(null)
            false
        } else {
            // this is the new selected movie:
            repository.setSelectedMovie(movie)
            true
        }
    }

    private fun isMovieAlreadySelected(imdbId: String): Boolean {
        return imdbId == selectedMovieLiveData.value?.imdbId
    }

    fun onMovieIsFavoriteChanged(movie: Movie) {
        if (movie.isFavorite){
            repository.addToFavorites(movie.imdbId)
        } else {
            repository.removeFromFavorites(movie.imdbId)
        }
    }

}