package com.church.injilkeselamatan.account_domain.use_case

import com.church.injilkeselamatan.account_domain.model.UserInfo
import com.church.injilkeselamatan.account_domain.repository.UserRepository

class SaveUserInfo(private val userRepository: UserRepository) {

    suspend operator fun invoke(userInfo: UserInfo) {
        userRepository.saveUserInfo(userInfo)
    }
}