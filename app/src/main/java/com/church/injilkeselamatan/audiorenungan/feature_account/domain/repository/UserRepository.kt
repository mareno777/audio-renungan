package com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository

import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.models.UserDto

interface UserRepository {

    suspend fun getUsers(): List<UserDto>

    suspend fun registerUser()
}