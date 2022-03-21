package com.church.injilkeselamatan.audio_domain.di

import android.content.Context
import com.church.injilkeselamatan.audio_domain.repository.SongRepository
import com.church.injilkeselamatan.audio_domain.use_case.*
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AudioDomainModule {

    @ViewModelScoped
    @Provides
    fun provideSongUseCases(
        repository: SongRepository,
        downloadManager: DownloadManager
    ): SongUseCases {
        return SongUseCases(
            getSongs = GetSongs(repository),
            loadRecentSong = LoadRecentSong(repository),
            getDownloadedSongs = GetDownloadedSongs(repository, downloadManager),
            updateSong = UpdateSong(repository),
            getFeaturedSong = GetFeaturedSong(repository)
        )
    }

    @ViewModelScoped
    @Provides
    fun provideAnotherUseCases(
        @ApplicationContext context: Context,
        songRepository: SongRepository
    ) =
        AnotherUseCases(
            copyToClipboard = CopyToClipboard(context),
            checkVersion = CheckVersion(songRepository),
            emailIntent = EmailIntent(context)
        )
}