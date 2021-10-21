package com.church.injilkeselamatan.audiorenungan.di

import android.content.ComponentName
import android.content.Context
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.SongsApi
import com.church.injilkeselamatan.audiorenungan.feature_music.data.repository.SongRepositoryImpl
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.GetDownloadedSongs
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.GetSongs
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.UpdateSong
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.util.ConnectionLiveData
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.MusicService
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.data.MusicSourceRepository
import com.google.android.exoplayer2.offline.DownloadManager
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
    fun providePersistentStorage(
        @ApplicationContext context: Context
    ) =
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
    fun provideSongRepository(songsApi: SongsApi, database: MusicDatabase): SongRepository =
        SongRepositoryImpl(songsApi, database)

    @Singleton
    @Provides
    fun provideSongUseCases(
        repository: SongRepository,
        downloadManager: DownloadManager
    ): SongUseCases {
        return SongUseCases(
            getSongs = GetSongs(repository),
            getDownloadedSongs = GetDownloadedSongs(repository, downloadManager),
            updateSong = UpdateSong(repository)
        )
    }

    @Singleton
    @Provides
    fun provideMusicSourceRepository(
        musicDatabase: MusicDatabase,
        @ApplicationContext context: Context
    ): MusicSourceRepository = MusicSourceRepository(musicDatabase, context)
}