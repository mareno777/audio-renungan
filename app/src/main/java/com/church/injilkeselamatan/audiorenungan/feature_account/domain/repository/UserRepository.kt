package com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository

import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.models.*
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUsers(): Flow<Resource<List<UserDto>>>

    suspend fun registerUser(createUserRequest: CreateUserRequest): Flow<Resource<UserApiDtoSingle>>

    suspend fun updateCredentials(updateUserRequest: UpdateUserRequest): Flow<Resource<UserApiDtoSingle>>

    suspend fun getIp(): Resource<GetIpAddress>
}