package com.ponap.filmfinder.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ponap.filmfinder.model.Movie
import com.ponap.filmfinder.model.MovieDetails
import com.ponap.filmfinder.model.MovieSearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepository @Inject constructor(
    private val remoteDataSource: MoviesRemoteDataSource
) {

    private val _selectedMovieLiveData: MutableLiveData<Movie?> = MutableLiveData(null)
    val selectedMovieLiveData: LiveData<Movie?>
        get() = _selectedMovieLiveData


    suspend fun searchMoviesByPage(text: String, page: Int): Result<MovieSearchResponse> {
        return remoteDataSource.searchMoviesByPage(text, page)
    }

    suspend fun fetchMovieDetails(imdbId: String): Result<MovieDetails> {
        return remoteDataSource.fetchMovieDetails(imdbId)
    }

    fun setSelectedMovie(movie: Movie?) {
        _selectedMovieLiveData.value = movie
    }

}