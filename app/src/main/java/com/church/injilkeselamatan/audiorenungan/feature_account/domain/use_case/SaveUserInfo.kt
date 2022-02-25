package com.church.injilkeselamatan.audiorenungan.feature_account.domain.use_case

import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.local.model.UserInfo
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository

class SaveUserInfo(private val userRepository: UserRepository) {

    suspend operator fun invoke(userInfo: UserInfo) {
        userRepository.saveUserInfo(userInfo)
    }
}