package com.church.injilkeselamatan.audiorenungan.di

import android.content.ComponentName
import android.content.Context
import com.church.injilkeselamatan.audiorenungan.data.SongRepository
import com.church.injilkeselamatan.audiorenungan.data.remote.SongsApi
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.uamp.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.uamp.media.MusicService
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
    fun provideSongRepository(@ApplicationContext context: Context, songsApi: SongsApi) =
        SongRepository(songsApi, context)
}