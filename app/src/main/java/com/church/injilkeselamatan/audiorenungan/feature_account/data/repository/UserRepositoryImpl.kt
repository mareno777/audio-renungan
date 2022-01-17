package com.church.injilkeselamatan.audiorenungan.feature_account.data.repository

import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.models.UserDto
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository
import io.ktor.client.*

class UserRepositoryImpl(private val client: HttpClient) : UserRepository {
    override suspend fun getUsers(): List<UserDto> {
        TODO("Not yet implemented")
    }

    override suspend fun registerUser() {
        TODO("Not yet implemented")
    }
}