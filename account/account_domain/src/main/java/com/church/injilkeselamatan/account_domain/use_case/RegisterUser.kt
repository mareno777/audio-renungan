package com.church.injilkeselamatan.account_domain.use_case

import com.church.injilkeselamatan.account_domain.model.CreateUserRequest
import com.church.injilkeselamatan.account_domain.model.UserApi
import com.church.injilkeselamatan.account_domain.repository.UserRepository

class RegisterUser(private val userRepository: UserRepository) {

    suspend operator fun invoke(createUserRequest: CreateUserRequest): Result<UserApi> {
        return userRepository.registerUser(createUserRequest)
    }
}