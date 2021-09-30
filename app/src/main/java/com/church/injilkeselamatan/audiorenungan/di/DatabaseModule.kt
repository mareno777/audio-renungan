package com.church.injilkeselamatan.audiorenungan.di

import android.content.Context
import androidx.room.Room
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDao
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase =
        Room.databaseBuilder(context, MusicDatabase::class.java, "Music.db")
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideMusicDao(database: MusicDatabase): MusicDao = database.musicDao()

}