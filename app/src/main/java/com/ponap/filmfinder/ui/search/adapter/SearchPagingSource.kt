package com.ponap.filmfinder.ui.search.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ponap.filmfinder.data.MediaRepository
import com.ponap.filmfinder.model.Media

class SearchPagingSource(
    private val repository: MediaRepository,
    private val query: String
) : PagingSource<Int, Media>() {

    private var totalResults = -1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> {
        if (query.isBlank()) {
            return LoadResult.Page(
                data = listOf(),
                nextKey = null,
                prevKey = null
            )
        }

        val pageNumber = if (totalResults < 0) 1 else params.key ?: 1
        val previousKey = if (pageNumber == 1) null else pageNumber - 1

        val response = repository.searchMediaByPage(text = query, page = pageNumber)

        if (response.isFailure) {
            val exception = response.exceptionOrNull() ?: Exception("Something went wrong")
            return LoadResult.Error(exception)
        }

        val list =
            response.getOrNull()?.mediaResults?.map {
                val media = Media.fromApiSearchResponse(it)
                media.isFavorite = repository.isFavorite(media.imdbId)
                media
            } ?: emptyList()
        totalResults = response.getOrNull()?.totalResults?.toIntOrNull() ?: 0
        return LoadResult.Page(
            data = list,
            nextKey = getNextPageNumber(pageNumber),
            prevKey = previousKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Media>): Int? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun getNextPageNumber(pageNumber: Int): Int? {
        return when {
            pageNumber * 10 < totalResults -> pageNumber + 1
            else -> null
        }
    }
}