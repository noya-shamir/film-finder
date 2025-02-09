package com.ponap.filmfinder.ui.search.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ponap.filmfinder.data.MoviesRepository
import com.ponap.filmfinder.model.Movie

class SearchPagingSource(
    private val repository: MoviesRepository,
    private val query: String
) : PagingSource<Int, Movie>() {

    private var totalResults = -1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        if (query.isBlank()) {
            return LoadResult.Page(
                data = listOf(),
                nextKey = null,
                prevKey = null
            )
        }

        val pageNumber = if (totalResults < 0) 1 else params.key ?: 1
        val previousKey = if (pageNumber == 1) null else pageNumber - 1

        val response = repository.searchMoviesByPage(text = query, page = pageNumber)

        if (response.isFailure) {
            val exception = response.exceptionOrNull() ?: Exception("Something went wrong")
            return LoadResult.Error(exception)
        }

        val list =
            response.getOrNull()?.movies?.map { Movie.fromApiSearchResponse(it) } ?: emptyList()
        totalResults = response.getOrNull()?.totalResults?.toIntOrNull() ?: 0
        return LoadResult.Page(
            data = list,
            nextKey = getNextPageNumber(pageNumber),
            prevKey = previousKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
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