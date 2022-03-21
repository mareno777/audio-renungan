package com.church.injilkeselamatan.audiorenungan.di

import android.content.Context
import com.church.injilkeselamatan.audio_domain.use_case.DownloadAudio
import com.church.injilkeselamatan.audiorenungan.services.AudioDownloadService
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioDownloadModule {

    @Provides
    @Singleton
    fun provideDownloadAudio(
        @ApplicationContext context: Context,
        downloadManager: DownloadManager
    )
    : DownloadAudio = DownloadAudio(
        context = context,
        downloadManager = downloadManager,
        AudioDownloadService::class.java
    )

}
