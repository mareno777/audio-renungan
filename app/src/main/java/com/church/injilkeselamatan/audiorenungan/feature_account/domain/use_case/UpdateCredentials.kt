package com.church.injilkeselamatan.audiorenungan.feature_account.domain.use_case

import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model.UpdateUserRequest
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model.UserApiDtoSingle
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository

class UpdateCredentials(private val userRepository: UserRepository) {

    suspend operator fun invoke(updateUserRequest: UpdateUserRequest): Result<UserApiDtoSingle> {
        return userRepository.updateCredentials(updateUserRequest)
    }
}