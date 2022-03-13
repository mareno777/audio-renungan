package com.church.injilkeselamatan.account_domain.use_case

import com.church.injilkeselamatan.account_domain.repository.UserRepository

class UserGetIp(private val userRepository: UserRepository) {

    suspend operator fun invoke(): String {
        val result = userRepository.getIp()
        return result.getOrNull()?.ipAddress ?: "Couldn't fetch the IP Address"
    }
}