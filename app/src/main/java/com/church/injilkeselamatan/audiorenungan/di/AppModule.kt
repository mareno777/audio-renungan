package com.church.injilkeselamatan.audiorenungan.di

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.SongsApi
import com.church.injilkeselamatan.audiorenungan.feature_music.data.repository.SongRepositoryImpl
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.*
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.util.ConnectionLiveData
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.MusicService
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.data.MusicSourceRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.MusicSource
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideSongRepository(
        songsApi: SongsApi,
        database: MusicDatabase
    ): SongRepository =
        SongRepositoryImpl(songsApi, database)

    @Singleton
    @Provides
    fun provideSongUseCases(
        @ApplicationContext context: Context,
        repository: SongRepository,
        downloadManager: DownloadManager
    ): SongUseCases {
        return SongUseCases(
            getSongs = GetSongs(repository),
            getDownloadedSongs = GetDownloadedSongs(repository, downloadManager),
            updateSong = UpdateSong(repository),
            downloadSong = DownloadSong(context, downloadManager)
        )
    }

    @Singleton
    @Provides
    fun provideMusicSourceRepository(
        @ApplicationContext context: Context,
        musicDatabase: MusicDatabase
    ): MusicSource = MusicSourceRepository(musicDatabase, context)

    @Provides
    @Singleton
    fun provideHardwareId(@ApplicationContext context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}