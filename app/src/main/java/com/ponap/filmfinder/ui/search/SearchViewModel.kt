package com.ponap.filmfinder.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ponap.filmfinder.data.MediaRepository
import com.ponap.filmfinder.model.Media
import com.ponap.filmfinder.ui.search.adapter.SearchPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    /** live data of the selected media, from the repository */
    val selectedMediaLiveData = repository.selectedMediaLiveData

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
        repository.setSelectedMedia(null)
    }

    fun getCurrentQuery() = currentQueryText

    fun isNewQuery(newText: String?): Boolean = newText?.trim() != currentQueryText

    /**
     * checks if this media is selected or un-selected, and informs the repository
     * @return true if the media was selected, false otherwise
     * */
    fun setSelectedMediaAsNeeded(media: Media): Boolean {
        return if (isMediaAlreadySelected(media.imdbId)) {
            // this is a case on un-selecting a media:
            repository.setSelectedMedia(null)
            false
        } else {
            // this is the new selected media:
            repository.setSelectedMedia(media)
            true
        }
    }

    private fun isMediaAlreadySelected(imdbId: String): Boolean {
        return imdbId == selectedMediaLiveData.value?.imdbId
    }

    fun onMediaIsFavoriteChanged(media: Media) {
        if (media.isFavorite){
            repository.addToFavorites(media.imdbId)
        } else {
            repository.removeFromFavorites(media.imdbId)
        }
    }

}