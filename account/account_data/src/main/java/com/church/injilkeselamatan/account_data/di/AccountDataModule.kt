package com.church.injilkeselamatan.account_data.di

import android.content.Context
import com.church.injilkeselamatan.account_data.UserRepositoryImpl
import com.church.injilkeselamatan.account_domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountDataModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        client: HttpClient,
        @ApplicationContext context: Context
    ): UserRepository = UserRepositoryImpl(client, context)
}