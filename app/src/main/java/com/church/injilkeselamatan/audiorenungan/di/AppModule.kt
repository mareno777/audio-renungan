package com.church.injilkeselamatan.audiorenungan.di

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import com.church.injilkeselamatan.audiorenungan.services.MusicService
import com.church.injilkeselamatan.core.MusicServiceConnection
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
    fun provideMusicServiceConnection(@ApplicationContext context: Context) =
        MusicServiceConnection(context, ComponentName(context, MusicService::class.java))


    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    fun provideHardwareId(@ApplicationContext context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}