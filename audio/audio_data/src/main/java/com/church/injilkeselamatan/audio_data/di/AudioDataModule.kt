package com.church.injilkeselamatan.audio_data.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.church.injilkeselamatan.audio_data.data_source.SongRepositoryImpl
import com.church.injilkeselamatan.audio_data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audio_data.data_source.local.PersistentStorage
import com.church.injilkeselamatan.audio_domain.helper.BrowseTree
import com.church.injilkeselamatan.audio_domain.repository.SongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioDataModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase =
        Room.databaseBuilder(context, MusicDatabase::class.java, "Music.db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providePersistentStorage(
        @ApplicationContext context: Context
    ) =
        PersistentStorage(context)

    @OptIn(ExperimentalCoilApi::class)
    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()

    @Singleton
    @Provides
    fun provideSongRepository(
        database: MusicDatabase,
        client: HttpClient,
        imageLoader: ImageLoader,
        @ApplicationContext context: Context
    ): SongRepository =
        SongRepositoryImpl(database, client, imageLoader, context)


    @Singleton
    @Provides
    fun provideBrowseTree(
        @ApplicationContext context: Context,
        songRepository: SongRepository
    ) = BrowseTree(context, songRepository)
}