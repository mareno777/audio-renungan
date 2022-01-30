package com.church.injilkeselamatan.audiorenungan.di

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import coil.ImageLoader
import coil.util.CoilUtils
import com.church.injilkeselamatan.audiorenungan.feature_account.data.repository.UserRepositoryImpl
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
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
import io.ktor.client.*
import okhttp3.OkHttpClient
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
        database: MusicDatabase,
        client: HttpClient
    ): SongRepository =
        SongRepositoryImpl(database, client)

    @Singleton
    @Provides
    fun provideSongUseCases(
        @ApplicationContext context: Context,
        repository: SongRepository,
        downloadManager: DownloadManager,
        musicSource: MusicSource
    ): SongUseCases {
        return SongUseCases(
            getSongs = GetSongs(repository, musicSource),
            getDownloadedSongs = GetDownloadedSongs(repository, downloadManager),
            updateSong = UpdateSong(repository),
            downloadSong = DownloadSong(context, downloadManager),
            getFeaturedSong = GetFeaturedSong(repository)
        )
    }

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = ImageLoader.Builder(context)
        .okHttpClient {
            OkHttpClient.Builder()
                .cache(CoilUtils.createDefaultCache(context))
                .build()
        }
        .build()

    @Singleton
    @Provides
    fun provideMusicSourceRepository(
        @ApplicationContext context: Context,
        musicDatabase: MusicDatabase,
        imageLoader: ImageLoader
    ): MusicSource = MusicSourceRepository(musicDatabase, context, imageLoader)

    @Singleton
    @Provides
    fun provideUserRepository(client: HttpClient): UserRepository =
        UserRepositoryImpl(client)

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    fun provideHardwareId(@ApplicationContext context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}