package com.church.injilkeselamatan.audiorenungan.di

import android.content.ComponentName
import android.content.Context
import com.church.injilkeselamatan.audiorenungan.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.MusicService
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.util.ConnectionLiveData
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
}