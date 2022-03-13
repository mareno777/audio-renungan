package com.church.injilkeselamatan.account_domain.use_case

import android.util.Log
import com.church.injilkeselamatan.account_domain.model.UpdateUserRequest
import com.church.injilkeselamatan.account_domain.model.UserApi
import com.church.injilkeselamatan.account_domain.repository.UserRepository

class UpdateCredentials(private val userRepository: UserRepository) {

    suspend operator fun invoke(updateUserRequest: UpdateUserRequest): Result<UserApi> {
        Log.i("UpdateCredentials", "updating credentials")
        return userRepository.updateCredentials(updateUserRequest)
    }
}