package com.church.injilkeselamatan.account_domain.di

import com.church.injilkeselamatan.account_domain.repository.UserRepository
import com.church.injilkeselamatan.account_domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AccountDomainModule {

    @ViewModelScoped
    @Provides
    fun provideUserUseCases(userRepository: UserRepository) = UserUseCases(
        saveUserInfo = SaveUserInfo(userRepository),
        loadUserInfo = LoadUserInfo(userRepository),
        userGetIp = UserGetIp(userRepository),
        registerUser = RegisterUser(userRepository),
        updateCredentials = UpdateCredentials(userRepository)
    )
}