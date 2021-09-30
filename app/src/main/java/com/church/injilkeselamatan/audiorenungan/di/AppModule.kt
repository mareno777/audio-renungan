package com.church.injilkeselamatan.audiorenungan.di

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.repository.SongRepositoryImpl
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.SongsApi
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.MusicService
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.util.ConnectionLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePersistentStoreage(@ApplicationContext context: Context): PersistentStorage =
        PersistentStorage(context)

    @Singleton
    @Provides
    fun provideMusicServiceConnection(@ApplicationContext context: Context) =
        MusicServiceConnection(context, ComponentName(context, MusicService::class.java))

    @Singleton
    @Provides
    fun provideConnectionLiveData(@ApplicationContext context: Context) =
        ConnectionLiveData(context)

    @Singleton
    @Provides
    fun provideApiService(): SongsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://injilkeselamatan.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SongsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSongRepository(@ApplicationContext context: Context, songsApi: SongsApi, database: MusicDatabase): SongRepository =
        SongRepositoryImpl(songsApi, context, database)

    @Singleton
    @Provides
    fun provideCurrrentPlaylist(): MutableList<MediaMetadataCompat> = mutableListOf()
}