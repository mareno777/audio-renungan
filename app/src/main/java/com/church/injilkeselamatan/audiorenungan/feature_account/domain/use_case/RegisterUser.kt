package com.church.injilkeselamatan.audiorenungan.feature_account.domain.use_case

import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model.CreateUserRequest
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model.UserApiDtoSingle
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository

class RegisterUser(private val userRepository: UserRepository) {

    suspend operator fun invoke(createUserRequest: CreateUserRequest): Result<UserApiDtoSingle> {
        return userRepository.registerUser(createUserRequest)
    }
}