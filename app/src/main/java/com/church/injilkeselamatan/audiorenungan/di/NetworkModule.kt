package com.church.injilkeselamatan.audiorenungan.di

import android.content.Context
import android.provider.Settings
import com.church.injilkeselamatan.audiorenungan.data.remote.SongsApi
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
object NetworkModule {

    @Provides
    fun provideApiService(): SongsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://injilkeselamatan.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SongsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHardwareId(@ApplicationContext context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}