package com.ponap.filmfinder.di

import com.ponap.filmfinder.BuildConfig
import com.ponap.filmfinder.network.BASE_URL
import com.ponap.filmfinder.network.OmdbKeyInterceptor
import com.ponap.filmfinder.network.OmdbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {

        val loggingLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else
            HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(loggingLevel)
            )
            .addInterceptor(OmdbKeyInterceptor())
            .build()
    }

    @Singleton
    @Provides
    @Named("moviesRetrofit")
    fun provideDogsRetrofit(
        okHttpClient: dagger.Lazy<OkHttpClient>
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient.get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideOmdbApi(@Named("moviesRetrofit") moviesRetrofit: Retrofit): OmdbService =
        moviesRetrofit.create(OmdbService::class.java)
}