package com.church.injilkeselamatan.audiorenungan.di

import com.church.injilkeselamatan.audiorenungan.BuildConfig
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.SongsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApiService(): SongsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SongsApi::class.java)
    }
}